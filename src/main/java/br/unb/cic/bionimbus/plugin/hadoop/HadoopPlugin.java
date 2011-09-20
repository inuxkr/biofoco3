package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PFileEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.AbstractMessage;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.GetReqMessage;
import br.unb.cic.bionimbus.p2p.messages.GetRespMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.p2p.messages.PrepReqMessage;
import br.unb.cic.bionimbus.p2p.messages.PrepRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StoreAckMessage;
import br.unb.cic.bionimbus.p2p.messages.TaskErrorMessage;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.utils.Pair;

public class HadoopPlugin implements Plugin, P2PListener, Runnable {

	private final String id = UUID.randomUUID().toString();
	
	private final ScheduledExecutorService schedExecutorService = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder().namingPattern("HadoopPlugin-%d").build());

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("HadoopPlugin-workers-%d").build());
	
	private Future<PluginInfo> fInfo = null;
	
	private PluginInfo myInfo = null;
	
	private String errorString = "Plugin is loading...";
	
	private final Map<String, Pair<PluginTask, Integer>> pendingTasks = new ConcurrentHashMap<String, Pair<PluginTask,Integer>>();

	private final Map<String, Pair<PluginTask, Future<PluginTask>>> executingTasks = new ConcurrentHashMap<String, Pair<PluginTask, Future<PluginTask>>>();
	
	private final List<Future<PluginFile>> pendingSaves = new CopyOnWriteArrayList<Future<PluginFile>>();
	
	private final List<Future<HadoopGetFile>> pendingGets = new CopyOnWriteArrayList<Future<HadoopGetFile>>();
	
	private final Map<String, PluginFile> pluginFiles = new ConcurrentHashMap<String, PluginFile>();
	
	private final Map<String, Pair<String, Integer>> workingFiles = new ConcurrentHashMap<String, Pair<String,Integer>>();

	private P2PService p2p;
	
	public Map<String, Pair<String, Integer>> getWorkingFiles() {
		return workingFiles;
	}

	@Override
	public void run() {
		System.out.println("running Plugin loop...");
		checkGetInfo();
		checkFinishedTasks();
		checkPendingSaves();
		checkPendingGets();
	}

	private Message buildFinishedTaskMsg(PluginTask t, Future<PluginTask> f) {
		Message msg;

		try {
			PluginTask task = f.get();

			// pegar arquivos de saida e mandar para o storage service (sincrono
			// ou assincrono?)
			// salvar ID na task

			msg = new EndMessage(p2p.getPeerNode(), task);
		} catch (Exception e) {
			msg = new TaskErrorMessage(p2p.getPeerNode(), id, t.getId(), e.getMessage());
		}

		return msg;
	}

	private void checkFinishedTasks() {
		Future<PluginTask> fTask;
		PluginTask task;

		for (Pair<PluginTask, Future<PluginTask>> pair : executingTasks.values()) {
			task = pair.first;
			fTask = pair.second;

			if (fTask.isDone()) {
				Message message = buildFinishedTaskMsg(task, fTask);
				executingTasks.remove(task.getId());
				p2p.broadcast(message); //TODO: isto é um broadcast?
			}
		}
	}

	private Message buildFinishedGetInfoMsg(PluginInfo info) {
		if (info == null)
			return new InfoErrorMessage(p2p.getPeerNode(), id, errorString);

		info.setId(id);
		return new InfoRespMessage(p2p.getPeerNode(), info);
	}

	private void checkGetInfo() {
		if (fInfo == null) {
			fInfo = executorService.submit(new HadoopGetInfo());
			return;
		}

		if (fInfo.isDone()) {
			try {
				myInfo = fInfo.get();
				myInfo.setHost(p2p.getPeerNode().getHost());
			} catch (Exception e) {
				errorString = e.getMessage();
			}
			fInfo = null;
		}
	}

	private void checkTaskStatus(PeerNode receiver, String taskId) {
		Pair<PluginTask, Future<PluginTask>> pair = executingTasks.get(taskId);
		StatusRespMessage msg = new StatusRespMessage(p2p.getPeerNode(), pair.first);
		p2p.sendMessage(receiver.getHost(), msg);
	}
	
	private void checkPendingSaves() {
		for (Future<PluginFile> f : pendingSaves) {
			if (f.isDone()) {
				try {
					PluginFile file = f.get();
					file.setPluginId(this.id);
					pendingSaves.remove(f);
					pluginFiles.put(file.getId(), file);
					StoreAckMessage msg = new StoreAckMessage(p2p.getPeerNode(), file);
					p2p.broadcast(msg);
				} catch (Exception e) {
					e.printStackTrace();
					//TODO criar mensagem de erro?
				}
			}
		}
	}
	
	private void checkPendingGets() {
		for (Future<HadoopGetFile> f : pendingGets) {
			if (f.isDone()) {
				try {
					HadoopGetFile get = f.get();
					pendingGets.remove(f);
					Message msg = new PrepRespMessage(p2p.getPeerNode(), myInfo, get.getPluginFile(), get.getTaskId());
					p2p.sendMessage(get.getReceiver().getHost(), msg);
				} catch (Exception e) {
					// TODO criar mensagem de erro?
					e.printStackTrace();
				}
			}
		}
	}

	private void storeFile(P2PEvent event) {
		P2PFileEvent fileEvent = (P2PFileEvent) event;
		Map<String, String> parms = fileEvent.getParms();

		if (parms.isEmpty()) {
			System.out.println("recebi arquivo " + fileEvent.getFile().getPath());
			Future<PluginFile> f = executorService.submit(new HadoopSaveFile(fileEvent.getFile().getPath()));
			pendingSaves.add(f);
			return;
		}
		
		String fileId = parms.get("fileId");
		String fileName = parms.get("fileName");
		Pair<String, Integer> workFile = workingFiles.get("fileId");
		int count = 0;
		if (workFile != null)
			count = workFile.second;
		count++;
		workingFiles.put(fileId, new Pair<String, Integer>(fileName, count));
			
		
		String taskId = parms.get("taskId");
		Pair<PluginTask, Integer> pair = pendingTasks.get(taskId);
		count = pair.second;
		if (--count == 0) {
			pendingTasks.remove(taskId);
			startTask(pair.first);
			return;
		}
		
		Pair<PluginTask, Integer> newPair = new Pair<PluginTask, Integer>(pair.first, count);
		pendingTasks.put(taskId, newPair);
	}

	private void getFileFromHadoop(PluginFile file, String taskId, PeerNode receiver) {
		Future<HadoopGetFile> f = executorService.submit(new HadoopGetFile(file, taskId, receiver, p2p.getConfig().getServerPath()));
		pendingGets.add(f);
	}

	@Override
	public void start() {
		System.out.println("starting Hadoop plugin...");
		schedExecutorService.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		executorService.shutdownNow();
		schedExecutorService.shutdownNow();
		p2p.remove(this);
	}

	@Override
	public void setP2P(P2PService p2p) {
		if (this.p2p != null)
			this.p2p.remove(this);

		this.p2p = p2p;

		if (this.p2p != null)
			this.p2p.addListener(this);
	}

	@Override
	public void onEvent(P2PEvent event) {
		if (event.getType().equals(P2PEventType.FILE)) {
			storeFile(event);
			return;
		} else if (!event.getType().equals(P2PEventType.MESSAGE))
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;
		
		PeerNode receiver = null;
		if (msg instanceof AbstractMessage) {
			receiver = ((AbstractMessage) msg).getPeer();
		}		

		switch (P2PMessageType.of(msg.getType())) {
		case INFOREQ:
			Message infoMsg = buildFinishedGetInfoMsg(myInfo);
			p2p.sendMessage(receiver.getHost(), infoMsg);
			break;
		case STARTREQ:
			JobInfo job = ((StartReqMessage)msg).getJobInfo();
			StartRespMessage resp = new StartRespMessage(p2p.getPeerNode(), job.getId(), prepareTask(job));
			p2p.sendMessage(receiver.getHost(), resp);
			break;
		case STATUSREQ:
			StatusReqMessage reqMsg = (StatusReqMessage) msg;
			checkTaskStatus(receiver, reqMsg.getTaskId());
			break;
		case PREPREQ:
			PrepReqMessage prepMsg = (PrepReqMessage) msg;
			getFileFromHadoop(prepMsg.getPluginFile(), prepMsg.getTaskId(), receiver);
			break;
		case GETRESP:
			GetRespMessage getMsg = (GetRespMessage) msg;
			p2p.sendMessage(getMsg.getPluginInfo().getHost(), new PrepReqMessage(p2p.getPeerNode(), getMsg.getPluginFile(), getMsg.getTaskId()));
			break;
		case PREPRESP:
			PrepRespMessage respMsg = (PrepRespMessage) msg;
			Map<String, String> parms = new HashMap<String, String>();
			parms.put("taskId", respMsg.getTaskId());
			parms.put("fileId", respMsg.getPluginFile().getId());
			parms.put("fileName", respMsg.getPluginFile().getPath());
			p2p.getFile(respMsg.getPluginInfo().getHost(), respMsg.getPluginFile().getPath(), parms);
			break;
		}
	}
	
	private PluginTask prepareTask(JobInfo job) {
		PluginService service = myInfo.getService(job.getServiceId());
		if (service == null)
			return null;

		PluginTask task = new PluginTask();
		task.setJobInfo(job);
		pendingTasks.put(task.getId(), new Pair<PluginTask, Integer>(task, job.getInputs().size()));
		for (String fileId : job.getInputs().keySet()) {
			p2p.broadcast(new GetReqMessage(p2p.getPeerNode(), fileId, task.getId()));
		}
		
		return task;
	}
	
	private PluginTask startTask(PluginTask task) {
		PluginService service = myInfo.getService(task.getJobInfo().getServiceId());
		if (service == null)
			return null;

		Future<PluginTask> fTask = executorService.submit(new HadoopTask(this, task, service, p2p.getConfig().getServerPath()));
		Pair<PluginTask, Future<PluginTask>> pair = Pair.of(task, fTask);
		executingTasks.put(task.getId(), pair);

		return task;
	}
}

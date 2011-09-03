package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.p2p.messages.TaskErrorMessage;
import br.unb.cic.bionimbus.plugin.Plugin;
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

	private final Map<String, Pair<PluginTask, Future<PluginTask>>> taskMap = new ConcurrentHashMap<String, Pair<PluginTask, Future<PluginTask>>>();

	private P2PService p2p;

	@Override
	public void run() {
		System.out.println("running Plugin loop...");
		checkGetInfo();
		checkFinishedTasks();	
	}

	private Message buildFinishedTaskMsg(PluginTask t, Future<PluginTask> f) {
		Message msg;

		try {
			PluginTask task = f.get();

			// pegar arquivos de saida e mandar para o storage service (sincrono
			// ou assincrono?)
			// salvar ID na task

			msg = new EndMessage(task);
		} catch (Exception e) {
			msg = new TaskErrorMessage(id, t.getId(), e.getMessage());
		}

		return msg;
	}

	private void checkFinishedTasks() {
		Future<PluginTask> fTask;
		PluginTask task;

		for (Pair<PluginTask, Future<PluginTask>> pair : taskMap.values()) {
			task = pair.first;
			fTask = pair.second;

			if (fTask.isDone()) {
				Message message = buildFinishedTaskMsg(task, fTask);
				taskMap.remove(task.getId());
				p2p.sendMessage(message);
			}
		}
	}

	private Message buildFinishedGetInfoMsg(PluginInfo info) {
		if (info == null)
			return new InfoErrorMessage(id, errorString);

		info.setId(id);
		return new InfoRespMessage(info);
	}

	private void checkGetInfo() {
		if (fInfo == null) {
			fInfo = executorService.submit(new HadoopGetInfo());
			return;
		}

		if (fInfo.isDone()) {
			try {
				myInfo = fInfo.get();
				fInfo = null;
			} catch (Exception e) {
				errorString = e.getMessage();
			}
		}
	}

	private void checkTaskStatus(String taskId) {
		Pair<PluginTask, Future<PluginTask>> pair = taskMap.get(taskId);
		StatusRespMessage msg = new StatusRespMessage(pair.first);
		p2p.sendMessage(msg);
	}

	private void storeFile() {
		// 1. Receber todo o arquivo
		// 2. Salvar arquivo no Hadoop
		// 3. Construir mensagem de sucesso (com caminho do arquivo) ou de erro
		// 4. Enviar mensagem pelo P2P.
	}

	private void getFile() {
		// 1. Baixar arquivo do Hadoop.
		// 2. Enviar arquivo todo ou mensagem de erro pelo P2P.
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
		if (event.getType() != P2PEventType.MESSAGE)
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;

		switch (P2PMessageType.values()[msg.getType()]) {
		case INFOREQ:
			Message infoMsg = buildFinishedGetInfoMsg(myInfo);
			p2p.sendMessage(infoMsg);
			break;
		case STARTREQ:
			JobInfo job = ((StartReqMessage)msg).getJobInfo();
			StartRespMessage resp = new StartRespMessage(job.getId(), startTask(job));
			p2p.sendMessage(resp);
			break;
		case STATUSREQ:
			StatusReqMessage reqMsg = (StatusReqMessage) msg;
			checkTaskStatus(reqMsg.getTaskId());
			break;
		case STOREREQ:
			storeFile();
			break;
		case GETREQ:
			getFile();
			break;
		}
	}
	
	private PluginTask startTask(JobInfo job) {
		PluginService service = myInfo.getService(job.getServiceId());
		if (service == null)
			return null;

		PluginTask task = new PluginTask();
		Future<PluginTask> fTask = executorService.submit(new HadoopTask(job, service, task));
		Pair<PluginTask, Future<PluginTask>> pair = Pair.of(task, fTask);
		taskMap.put(task.getId(), pair);

		return task;
	}
}
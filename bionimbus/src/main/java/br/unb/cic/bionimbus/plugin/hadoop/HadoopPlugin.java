package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.p2p.messages.TaskErrorMessage;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.utils.Pair;

public class HadoopPlugin implements Plugin, P2PListener, Callable<Boolean> {

	private volatile boolean running = false;
	
	private final String id = UUID.randomUUID().toString();

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("hadoopplugin-%d").build());

	private final List<Future<PluginInfo>> reqList = new CopyOnWriteArrayList<Future<PluginInfo>>();

	private final Map<String, Pair<PluginTask, Future<PluginTask>>> taskMap = new ConcurrentHashMap<String, Pair<PluginTask, Future<PluginTask>>>();

	private P2PService p2p;

	@Override
	public Boolean call() throws Exception {
		running = true;

		while (running) {
			System.out.println("running Plugin loop...");
			checkFinishedGetInfo();
			checkFinishedTasks();
			Thread.sleep(10000);
		}

		return true;
	}

	private Message buildFinishedTaskMsg(PluginTask t, Future<PluginTask> f) {
		Message msg;

		try {
			PluginTask task = f.get();

			// pegar arquivos de saida e mandar para o storage service (sincrono ou assincrono?)
			// salvar ID na task

			msg = new EndMessage(task);
		} catch (Exception e) {
			msg = new TaskErrorMessage(id, t.getId(), e.getMessage());
		}

		return msg;
	}

	private void checkFinishedTasks() {
		Pair<PluginTask, Future<PluginTask>> pair;
		Future<PluginTask> fTask;
		PluginTask task;
		Iterator<Pair<PluginTask, Future<PluginTask>>> it = taskMap.values().iterator();

		while (it.hasNext()) {
			pair = it.next();
			task = pair.first;
			fTask = pair.second;

			if (fTask.isDone()) {
				Message message = buildFinishedTaskMsg(task, fTask);
				taskMap.remove(task.getId());
				p2p.sendMessage(message);
			}
		}
	}

	private Message buildFinishedGetInfoMsg(Future<PluginInfo> f) {
		Message msg;

		try {
			PluginInfo info = f.get();
			info.setId(id);
			msg = new InfoRespMessage(info);
		} catch (Exception e) {
			msg = new InfoErrorMessage(id, e.getMessage());
		}

		return msg;
	}

	private void checkFinishedGetInfo() {
		for (Future<PluginInfo> fInfo : reqList) {
			if (fInfo.isDone()) {
				Message message = buildFinishedGetInfoMsg(fInfo);
				reqList.remove(fInfo);
				p2p.sendMessage(message);
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
		executorService.submit(this);
	}

	@Override
	public void shutdown() {
		running = false;
		executorService.shutdownNow();
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
		Message msg = event.getMessage();

		if (msg == null)
			return;

		switch (P2PMessageType.values()[msg.getType()]) {
		case INFOREQ:
			Future<PluginInfo> fInfo = executorService.submit(new HadoopGetInfo());
			reqList.add(fInfo);
			break;
		case STARTREQ:
			PluginTask task = new PluginTask();
			Future<PluginTask> fTask = executorService.submit(new HadoopTask(task));
			Pair<PluginTask, Future<PluginTask>> pair = Pair.of(task, fTask);
			taskMap.put(task.getId(), pair);
			// TODO enviar mensagem de taskId como resposta.
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
}

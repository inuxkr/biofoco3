package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.p2p.BioNimbusP2P;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PException;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.FinishedTaskMessage;
import br.unb.cic.bionimbus.p2p.messages.Message;
import br.unb.cic.bionimbus.p2p.messages.PluginInfoMessage;
import br.unb.cic.bionimbus.p2p.messages.TaskErrorMessage;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class HadoopPlugin implements Plugin, P2PListener, Callable<Boolean> {

	private volatile boolean running = false;

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("hadoopplugin-%d").build());

	private final List<Future<PluginInfo>> reqList = new LinkedList<Future<PluginInfo>>();

	private final Map<PluginTask, Future<PluginTask>> taskMap = new HashMap<PluginTask, Future<PluginTask>>();

	private BioNimbusP2P p2p;

	@Override
	public Boolean call() throws Exception {

		running = true;

		while (running) {
			System.out.println("running Plugin loop...");
			checkFinishedGetInfo();
			checkFinishedTasks();
			Thread.sleep(5000);
		}

		return true;
	}

	private Message buildFinishedTaskMsg(PluginTask t, Future<PluginTask> f) {
		Message msg;
		
		try {
			PluginTask task = f.get();

			// pegar arquivos de saida e mandar para o storage service (sincrono ou assincrono?)
			// salvar ID na task

			msg = new FinishedTaskMessage(task);

		} catch (Exception e) {
			msg = new TaskErrorMessage(t.getID(), e.getMessage());
		}

		return msg;
	}

	private void checkFinishedTasks() throws P2PException {
		Future<PluginTask> fTask;
		PluginTask task;

		for (Entry<PluginTask, Future<PluginTask>> entry : taskMap.entrySet()) {

			task = entry.getKey();
			fTask = entry.getValue();

			if (fTask.isDone()) {
				Message message = buildFinishedTaskMsg(task, fTask);
				taskMap.remove(task);
				p2p.sendMessage(message);
			}
		}
	}
	
	private Message buildFinishedGetInfoMsg(Future<PluginInfo> f) {
		Message msg;

		try {
			PluginInfo info = f.get();
			msg = new PluginInfoMessage(info);
		} catch (Exception e) {
			msg = new ErrorMessage(e.getMessage());
		}

		return msg;
	}

	private void checkFinishedGetInfo() throws P2PException {

		for (Future<PluginInfo> fInfo : reqList) {

			if (fInfo.isDone()) {
				Message message = buildFinishedGetInfoMsg(fInfo);
				reqList.remove(fInfo);
				p2p.sendMessage(message);
			}
		}
	}

	private void checkTaskStatus() {
		// 1. Pegar ID da task.
		// 2. Construir mensagem com seu ID e status.
		// 3. Enviar via P2P.
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
	public void setP2P(BioNimbusP2P p2p) {
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

		switch (msg.getType()) {
		case GETINFO:
			Future<PluginInfo> fInfo = executorService.submit(new HadoopGetInfo());
			reqList.add(fInfo);
			break;
		case STARTTASK:
			PluginTask task = new PluginTask();
			Future<PluginTask> fTask = executorService.submit(new HadoopTask(task));
			taskMap.put(task, fTask);
			// TODO enviar mensagem de taskId como resposta.
			break;
		case TASKSTATUS:
			checkTaskStatus();
			break;
		case STOREFILE:
			storeFile();
			break;
		case GETFILE:
			getFile();
			break;
		}
	}
}

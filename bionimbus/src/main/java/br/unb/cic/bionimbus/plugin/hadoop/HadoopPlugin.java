package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.p2p.BioNimbusP2P;
import br.unb.cic.bionimbus.p2p.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;

public class HadoopPlugin implements Plugin, P2PListener, Callable<Boolean> {

	private volatile boolean running = false;

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("hadoopplugin-%d").build());

	private final List<Future<PluginInfo>> reqList = new LinkedList<Future<PluginInfo>>();

	private final Map<Future<PluginTask>, PluginTask> taskMap = new HashMap<Future<PluginTask>, PluginTask>();

	private BioNimbusP2P p2p;

	@Override
	public Boolean call() throws Exception {

		running = true;
		
		p2p.addListener(this);

		// detectar se hadoop esta' rodando, logar erro se for o caso e sair.

		while (running) {
			System.out.println("running Plugin loop...");

			// aguardar nova requisicao com timeout			

			// verifica tipo de mensagem


			int i = 1;

			if (i == 0) {
				checkFinishedGetInfo();
			} else if (i == 1) {
				checkTasksStatus();
			}
			
			Thread.sleep(5000);
		}

		return true;
	}

	private void checkTasksStatus() throws InterruptedException, ExecutionException {
		Future<PluginTask> fTask;
		PluginTask task;
		for (Entry<Future<PluginTask>, PluginTask> e : taskMap.entrySet()) {
			fTask = e.getKey();
			if (fTask.isDone()) {
				taskMap.remove(fTask);
				task = fTask.get();
				if (task.getState() == PluginTaskState.DONE)
					System.out.println("task " + task.getID() + " is done");
				else
					System.out.println("algo de errado");
			} else {
				task = e.getValue();
				switch (task.getState()) {
				case WAITING:
					System.out.println("task " + task.getID() + " is waiting");
					break;
				case RUNNING:
					System.out.println("task " + task.getID() + " is running");
					break;
				default:
					System.out.println("algo de errado2");
				}

			}
		}
	}

	private void checkFinishedGetInfo() {

		for (Future<PluginInfo> fInfo : reqList) {
			
			if (fInfo.isDone()) {
				reqList.remove(fInfo);
				try {
					PluginInfo info = fInfo.get();
					System.out.println("FsSize: " + info.getFsSize());
					System.out.println("FsFreeSize: " + info.getFsFreeSize());
					System.out.println("NumCores: " + info.getNumCores());
					System.out.println("NumNodes: " + info.getNumNodes());
				} catch (Exception e) {
					// Logar erro
					System.out.println("info: 0");
				}
			} else if (fInfo.isCancelled()) {
				System.out.println("req cancelled");
			}
		}
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
	}

	@Override
	public void setP2P(BioNimbusP2P p2p) {
		this.p2p = p2p;		
	}

	@Override
	public void onEvent(P2PEvent event) {
		Message msg;
		
		switch (msg.getID()) {
		case GETINFO:
			Future<PluginInfo> fInfo = executorService.submit(new HadoopGetInfo());
			reqList.add(fInfo);
			break;
		case STARTTASK:
			PluginTask task = new PluginTask();
			Future<PluginTask> fTask = executorService.submit(new HadoopTask(task));				
			taskMap.put(fTask, task);
			break;
		}
		
	}
}

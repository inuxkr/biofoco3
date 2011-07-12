package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class HadoopPlugin implements Plugin, Callable<Boolean> {

	private volatile boolean running = false;

	private ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("hadoopplugin-%d").build());

	private LinkedList<Future<PluginInfo>> reqList;

	public HadoopPlugin() {
		reqList = new LinkedList<Future<PluginInfo>>();
	}

	@Override
	public Boolean call() throws Exception {

		running = true;

		// detectar se hadoop esta' rodando, logar erro se for o caso e sair.

		while (running) {
			System.out.println("running Plugin loop...");

			// aguardar nova requisicao com timeout

			// verifica tipo de mensagem

			Future<PluginInfo> f = executorService.submit(new HadoopGetInfo());
			reqList.add(f);
			
			//executorService.submit(new HadoopStartTask());

			// se for startTask()
			// iniciar tarefa requisitada no hadoop
			// se for getTaskStatus()
			// verificar andamento da tarefa no hadoop

			// verificar andamento de todas as tarefas iniciadas e
			// enviar para o MonitorService via P2P.

			Iterator<Future<PluginInfo>> it = reqList.iterator();
			while (it.hasNext()) {
				f = it.next();
				if (f.isDone()) {
					reqList.remove(f);
					PluginInfo info = f.get();
					System.out.println("info: " + info.getFsSize());
				}
			}
			Thread.sleep(5000);
		}

		return true;
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
	public void setP2P() {
		// TODO Auto-generated method stub
	}

}

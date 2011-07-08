package br.unb.cic.bionimbus.plugin.hadoop;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.unb.cic.bionimbus.plugin.Plugin;

public class HadoopPlugin implements Plugin, Callable<Boolean> {

	private volatile boolean running = false;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	public HadoopPlugin() {

	}

	@Override
	public Boolean call() {
		
		running = true;

		// detectar se hadoop esta' rodando, logar erro se for o caso e sair.

		while (running) {
			System.out.println("running Plugin loop...");

			// aguardar nova requisicao com timeout

			// verifica tipo de mensagem

			// se for getInfo()
			// detectar dados do Hadoop
			// se for startTask()
			// iniciar tarefa requisitada no hadoop
			// se for getTaskStatus()
			// verificar andamento da tarefa no hadoop

			// verificar andamento de todas as tarefas iniciadas e
			// enviar para o MonitorService via P2P.

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

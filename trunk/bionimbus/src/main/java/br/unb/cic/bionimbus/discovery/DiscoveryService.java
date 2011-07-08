package br.unb.cic.bionimbus.discovery;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;

public class DiscoveryService implements Service, Callable<Boolean> {

	private volatile boolean running = false;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public Boolean call() {

		running = true;

		while (running) {
			System.out.println("running DiscoveryService...");

			// java.util.concurrent 1.5/1.6

			// executors.submit(new Worker());

			// aguardar nova requisicao com timeout

			// pegar dados de todos os plugins via broadcast no P2P.

			// se for getCloudInfo()
			// retornar dados de todos os plugins consolidados

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
		System.out.println("starting DiscoveryService...");
		executorService.submit(this);
	}

	@Override
	public void shutdown() {
		running = false;
		executorService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

}

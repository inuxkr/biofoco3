package br.unb.cic.bionimbus.discovery;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;

public class DiscoveryService implements Service, Callable<Boolean> {

	private volatile boolean running = false;

	private ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("discoveryservice-%d").build());

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public Boolean call() throws Exception {

		running = true;

		while (running) {
			System.out.println("running DiscoveryService...");

			// java.util.concurrent 1.5/1.6

			// executors.submit(new Worker());

			// aguardar nova requisicao com timeout

			// pegar dados de todos os plugins via broadcast no P2P.

			// se for getCloudInfo()
			// retornar dados de todos os plugins consolidados

			Thread.sleep(5000);
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

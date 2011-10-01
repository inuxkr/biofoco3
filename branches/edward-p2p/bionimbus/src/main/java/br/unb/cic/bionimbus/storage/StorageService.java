package br.unb.cic.bionimbus.storage;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;

public class StorageService implements Service, P2PListener, Callable<Boolean> {

	private volatile boolean running = false;

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("storageservice-%d").build());

	private P2PService p2p = null;

	public StorageService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public Boolean call() throws Exception {

		running = true;

		if (p2p != null)
			p2p.addListener(this);

		while (running) {
			System.out.println("running StorageService...");
			// loop para checar finalizacao das threads de salvamento e envio
			// de arquivos

			Thread.sleep(10000);
		}

		return true;
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		System.out.println("starting StorageService...");
		executorService.submit(this);
	}

	@Override
	public void shutdown() {
		running = false;
		p2p.remove(this);
		executorService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(P2PEvent event) {
		// tratar mensagem de storeFile() e de getFile()
		// criar as respectivas threads e salva requisicoes
		// para checagem do loop
	}

}
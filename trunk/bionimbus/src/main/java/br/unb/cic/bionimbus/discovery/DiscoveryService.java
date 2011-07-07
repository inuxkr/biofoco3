package br.unb.cic.bionimbus.discovery;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;

public class DiscoveryService extends Thread implements Service {

	public DiscoveryService(ServiceManager manager) {
		super("DiscoveryService");
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("running DiscoveryService...");
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		System.out.println("starting DiscoveryService...");
		super.start();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

}

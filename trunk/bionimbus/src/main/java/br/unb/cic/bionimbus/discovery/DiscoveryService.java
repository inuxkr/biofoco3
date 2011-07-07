package br.unb.cic.bionimbus.discovery;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;

public class DiscoveryService implements Service {

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public void start() {
		System.out.println("starting DiscoveryService...");

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

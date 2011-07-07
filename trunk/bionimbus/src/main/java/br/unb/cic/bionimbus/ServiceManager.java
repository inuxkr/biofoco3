package br.unb.cic.bionimbus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.unb.cic.bionimbus.discovery.DiscoveryService;

public class ServiceManager {

	List<Service> services;

	public ServiceManager() {
		services = new ArrayList<Service>();
		new DiscoveryService(this);
	}

	public void register(Service service) {
		services.add(service);
	}

	public void startAll() {
		Iterator<Service> it = services.iterator();

		while (it.hasNext())
			it.next().start();
	}
}

package br.unb.cic.bionimbus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.unb.cic.bionimbus.discovery.DiscoveryService;
import br.unb.cic.bionimbus.p2p.BioNimbusP2P;

public class ServiceManager {

	List<Service> services;

	public ServiceManager() {
		services = new ArrayList<Service>();
		new DiscoveryService(this);
	}

	public void register(Service service) {
		services.add(service);
	}

	public void startAll(BioNimbusP2P p2p) {
		Iterator<Service> it = services.iterator();

		while (it.hasNext())
			it.next().start(p2p);
	}
}

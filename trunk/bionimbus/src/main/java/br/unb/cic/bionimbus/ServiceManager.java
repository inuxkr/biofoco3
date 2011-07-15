package br.unb.cic.bionimbus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.unb.cic.bionimbus.discovery.DiscoveryService;
import br.unb.cic.bionimbus.monitor.MonitorService;
import br.unb.cic.bionimbus.p2p.BioNimbusP2P;
import br.unb.cic.bionimbus.sched.SchedService;
import br.unb.cic.bionimbus.storage.StorageService;

public class ServiceManager {

	List<Service> services;

	public ServiceManager() {
		services = new ArrayList<Service>();
		new DiscoveryService(this);
		new StorageService(this);
		new SchedService(this);
		new MonitorService(this);
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

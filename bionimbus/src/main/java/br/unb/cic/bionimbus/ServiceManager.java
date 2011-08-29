package br.unb.cic.bionimbus;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.bionimbus.discovery.DiscoveryService;
import br.unb.cic.bionimbus.monitor.MonitorService;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.sched.SchedService;
import br.unb.cic.bionimbus.storage.StorageService;

public class ServiceManager {

	private final List<Service> services = new ArrayList<Service>();

	public ServiceManager() {
		new DiscoveryService(this);
		new StorageService(this);
		new SchedService(this);
		new MonitorService(this);
	}

	public void register(Service service) {
		services.add(service);
	}

	public void startAll(P2PService p2p) {		
		for (Service service : services) {
			service.start(p2p);
		}
	}
}

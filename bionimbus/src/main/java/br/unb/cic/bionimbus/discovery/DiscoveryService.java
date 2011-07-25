package br.unb.cic.bionimbus.discovery;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.p2p.BioNimbusP2P;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PException;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.messages.CloudInfoMessage;
import br.unb.cic.bionimbus.p2p.messages.GetInfoMessage;
import br.unb.cic.bionimbus.p2p.messages.Message;
import br.unb.cic.bionimbus.p2p.messages.PluginInfoMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class DiscoveryService implements Service, P2PListener,
		Callable<Boolean> {

	private volatile boolean running = false;

	private final Map<String, PluginInfo> infoMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("discoveryservice-%d").build());

	private BioNimbusP2P p2p;

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public Boolean call() throws Exception {

		running = true;

		if (p2p != null)
			p2p.addListener(this);

		while (running) {
			System.out.println("running DiscoveryService...");

			Message msg = new GetInfoMessage();
			p2p.sendMessage(msg);

			Thread.sleep(30000);
		}

		return true;
	}

	@Override
	public void start(BioNimbusP2P p2p) {
		this.p2p = p2p;
		System.out.println("starting DiscoveryService...");
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
		Message msg = event.getMessage();
		if (msg == null)
			return;

		switch (msg.getType()) {
		case PLUGININFO:
			PluginInfoMessage pluginInfoMsg = (PluginInfoMessage) msg;
			infoMap.put(pluginInfoMsg.getInfo().getId(), pluginInfoMsg.getInfo());
			break;
		case CLOUDINFO:
			CloudInfoMessage cloudInfoMsg = new CloudInfoMessage(infoMap.values());
			try {
				p2p.sendMessage(cloudInfoMsg);
			} catch (P2PException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

}

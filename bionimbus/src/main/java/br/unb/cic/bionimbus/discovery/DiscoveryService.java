package br.unb.cic.bionimbus.discovery;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoReqMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class DiscoveryService implements Service, P2PListener,
		Callable<Boolean> {

	private volatile boolean running = false;

	private final Map<String, PluginInfo> infoMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("discoveryservice-%d").build());

	private P2PService p2p;

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

			Message msg = new InfoReqMessage();
			p2p.sendMessage(msg);

			Thread.sleep(30000);
		}

		return true;
	}

	@Override
	public void start(P2PService p2p) {
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

		switch (P2PMessageType.values()[msg.getType()]) {
		case INFORESP:
			InfoRespMessage infoMsg = (InfoRespMessage) msg;
			infoMap.put(infoMsg.getPluginInfo().getId(), infoMsg.getPluginInfo());
			break;
		case CLOUDREQ:
			CloudReqMessage cloudInfoMsg = new CloudReqMessage(infoMap.values());
			p2p.sendMessage(cloudInfoMsg);
			break;
		}
	}

}

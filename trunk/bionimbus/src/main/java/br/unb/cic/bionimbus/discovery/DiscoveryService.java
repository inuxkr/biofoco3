package br.unb.cic.bionimbus.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.AbstractMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoReqMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class DiscoveryService implements Service, P2PListener, Runnable {
	
	private static final int PERIOD_SECS = 30;

	private final Map<String, PluginInfo> infoMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ScheduledExecutorService schedExecService = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder().namingPattern("DiscoveryService-%d").build());

	private P2PService p2p;

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running DiscoveryService...");
		Message msg = new InfoReqMessage(p2p.getPeerNode());
		p2p.broadcast(msg);

		long now = System.currentTimeMillis();
		for (PluginInfo plugin : infoMap.values()) {
			if (now - plugin.getTimestamp() > 3*PERIOD_SECS) {
				infoMap.remove(plugin.getId());
			}
		}
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, PERIOD_SECS, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		p2p.remove(this);
		schedExecService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(P2PEvent event) {
		if (!event.getType().equals(P2PEventType.MESSAGE))
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		
		if (msg == null)
			return;
		
		PeerNode sender = p2p.getPeerNode();
		PeerNode receiver = null;
		if (msg instanceof AbstractMessage) {
			receiver = ((AbstractMessage) msg).getPeer();
		}

		switch (P2PMessageType.of(msg.getType())) {
		case INFORESP:
			InfoRespMessage infoMsg = (InfoRespMessage) msg;
			PluginInfo info = infoMsg.getPluginInfo();
			info.setUptime(receiver.uptime());
			info.setLatency(receiver.getLatency());
			info.setTimestamp(System.currentTimeMillis());
			infoMap.put(infoMsg.getPluginInfo().getId(), infoMsg.getPluginInfo());
			break;
		case CLOUDREQ:
			CloudRespMessage cloudMsg = new CloudRespMessage(sender, infoMap.values());
			if (receiver != null)
				p2p.sendMessage(receiver.getHost(), cloudMsg);
			break;
		case ERROR:
			ErrorMessage errMsg = (ErrorMessage) msg;
			System.out.println("ERROR: type="
					+ errMsg.getErrorType().toString() + ";msg="
					+ errMsg.getError());
			break;
		}
	}

}

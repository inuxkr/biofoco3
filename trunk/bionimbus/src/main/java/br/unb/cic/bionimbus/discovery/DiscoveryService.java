package br.unb.cic.bionimbus.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoReqMessage;
import br.unb.cic.bionimbus.p2p.messages.InfoRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class DiscoveryService implements Service, P2PListener, Runnable {

	private final Map<String, PluginInfo> infoMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ScheduledExecutorService schedExecService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("DiscoveryService-%d").build());

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("DiscoveryService-workers-%d").build());

	private P2PService p2p;

	public DiscoveryService(ServiceManager manager) {
		System.out.println("registering DiscoveryService...");
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running DiscoveryService...");
		Message msg = new InfoReqMessage();
		p2p.sendMessage(msg);
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		p2p.remove(this);
		executorService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(P2PEvent event) {
		if (event.getType() != P2PEventType.MESSAGE)
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;

		switch (P2PMessageType.values()[msg.getType()]) {
		case INFORESP:
			InfoRespMessage infoMsg = (InfoRespMessage) msg;
			infoMap.put(infoMsg.getPluginInfo().getId(),
					infoMsg.getPluginInfo());
			break;
		case CLOUDREQ:
			CloudReqMessage cloudInfoMsg = new CloudReqMessage(infoMap.values());
			p2p.sendMessage(cloudInfoMsg);
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

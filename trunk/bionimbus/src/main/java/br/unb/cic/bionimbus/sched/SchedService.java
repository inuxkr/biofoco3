package br.unb.cic.bionimbus.sched;

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
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;

public class SchedService implements Service, P2PListener, Runnable {

	private final ScheduledExecutorService schedExecService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("SchedService-%d").build());

	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("schedservice-%d").build());

	private P2PService p2p = null;

	public SchedService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running SchedService...");
		Message msg = new CloudReqMessage();
		p2p.sendMessage(msg);
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
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
		case CLOUDRESP:
			CloudRespMessage cloudMsg = (CloudRespMessage) msg;
			for (PluginInfo info : cloudMsg.values()) {
				System.out.println("plugin = " + info.getId());
				System.out.println("fsSize = " + info.getFsSize());
				System.out.println("fsFreeSize = " + info.getFsFreeSize());
				System.out.println("numNodes = " + info.getNumNodes());
				System.out.println("numCores = " + info.getNumCores());
				for (PluginService serv : info.getServices()) {
					System.out.println("\tservice 1 = " + serv.getName());
				}
				System.out.println();
			}
		}
	}

}

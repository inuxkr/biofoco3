package br.unb.cic.bionimbus.p2p;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.messaging.MessageListener;
import br.unb.cic.bionimbus.messaging.MessageService;
import br.unb.cic.bionimbus.p2p.messages.PingReqMessage;

public class P2PService implements MessageListener {

	private final MessageService msgService = new MessageService();

	private final List<P2PListener> listeners = new CopyOnWriteArrayList<P2PListener>();
	
	private final PeerNode peerNode;
	
	private final ChordRing chord;

	private final BioNimbusConfig config;
	
	private final Set<Host> seeds = new CopyOnWriteArraySet<Host>();
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		
	public P2PService(BioNimbusConfig config) {
		this.peerNode = PeerFactory.createPeer(true);
		peerNode.setHost(config.getHost());		
		chord = new ChordRing(peerNode);
		
		seeds.addAll(config.getSeeds());
		
		this.config = config;
	}

	public void start() {
		
		List<Integer> types = new ArrayList<Integer>();

		for (P2PMessageType enumType : P2PMessageType.values())
			types.add(enumType.ordinal());

		msgService.bind(new InetSocketAddress(config.getHost().getAddress(), config.getHost().getPort()));
		msgService.addListener(this, types);
		msgService.start(new P2PMessageFactory());
		
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("chord").build();
		executor = Executors.newScheduledThreadPool(3, threadFactory);
		
		startSeedCollectionChecking();
		
	}

	private void startSeedCollectionChecking() {
		executor.scheduleAtFixedRate(new SeedChecker(), 0, 30, TimeUnit.SECONDS);
	}

	public void shutdown() {
		msgService.shutdown();
		executor.shutdownNow();
	}

	public boolean isMaster() {
		return true;
	}
	
	public void broadcast(Message message) {
		for (PeerNode node : chord.getRing()) {
			Host host = node.getHost();
			msgService.sendMessage(new InetSocketAddress(host.getAddress(), host.getPort()), message);
		}
	}
	
	public void sendMessage(Host host, Message message) {
		//TODO: ESSE METODO TEM QUE FUNCIONAR!
		msgService.sendMessage(new InetSocketAddress(host.getAddress(), host.getPort()), message);
	}
	
	public void sendMessage(Message message) {
		msgService.sendMessage(new InetSocketAddress("localhost", 9999), message);
	}

	public void addListener(P2PListener listener) {
		listeners.add(listener);
	}

	public void remove(P2PListener listener) {
		listeners.remove(listener);
	}

	//Todo: CONSERTAR?
//	@Override
//	public void onEvent(Host host, Message message) {
//		for (P2PListener listener : listeners) {
//			P2PEvent event = new P2PMessageEvent(host, message);
//			listener.onEvent(event);
//		}
//	}

	@Override
	public void onEvent(Message message) {
		// TODO Auto-generated method stub
		
	}

	public List<Host> getSeeds() {
		return new ArrayList<Host>(config.getSeeds());
	}
	
	private class SeedChecker implements Runnable {

		@Override
		public void run() {
			for (Host host: seeds) {
				sendMessage(host, new PingReqMessage());
			}
		}
		
	}
}

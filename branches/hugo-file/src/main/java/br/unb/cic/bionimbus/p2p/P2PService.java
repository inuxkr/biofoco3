package br.unb.cic.bionimbus.p2p;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.messaging.FileListener;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.messaging.MessageListener;
import br.unb.cic.bionimbus.messaging.MessageService;
import br.unb.cic.bionimbus.p2p.messages.PingReqMessage;
import br.unb.cic.bionimbus.p2p.messages.PingRespMessage;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class P2PService implements MessageListener, FileListener {

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
	
	public BioNimbusConfig getConfig() {
		return config;
	}

	public void start() {
		
		List<Integer> types = new ArrayList<Integer>();

		for (P2PMessageType enumType : P2PMessageType.values())
			types.add(enumType.code());

		msgService.bind(new InetSocketAddress(config.getHost().getAddress(), config.getHost().getPort()));
		msgService.addListener(this, types);
		msgService.addFileListener(this);
		msgService.start(new P2PMessageFactory(), this.config);
		
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("chord").build();
		executor = Executors.newScheduledThreadPool(3, threadFactory);
		
		startSeedCollectionChecking();
		
	}

	private void startSeedCollectionChecking() {
		executor.scheduleAtFixedRate(new SeedFinger(), 0, 30, TimeUnit.SECONDS);
	}

	public void shutdown() {
		msgService.shutdown();
		executor.shutdownNow();
	}

	public boolean isMaster() {
		return true;
	}
	
	public void broadcast(Message message) {
		
		// send message to self
		Host myHost = this.peerNode.getHost();
		sendMessage(myHost, message);
		
		// send message to known peers
		for (PeerNode node : chord.peers()) {
			Host host = node.getHost();
			sendMessage(host, message);
		}
	}
	
	public void sendMessage(Host host, Message message) {
		msgService.sendMessage(new InetSocketAddress(host.getAddress(), host.getPort()), message);
	}
	
	public void sendFile(Host host, String fileName) {
		msgService.sendFile(new InetSocketAddress(host.getAddress(), host.getPort()), fileName);
	}
	
	public void getFile(Host host, String fileName) {
		Map<String, String> emptyMap = Collections.emptyMap();
		this.getFile(host, fileName, emptyMap);
	}
	
	public void getFile(Host host, String fileName, Map<String, String> parms) {
		msgService.getFile(new InetSocketAddress(host.getAddress(), host.getPort()), fileName, parms);
	}

	@Override
	public void onFileRecvd(File file, Map<String, String> parms) {
		for (P2PListener listener : listeners) {
			P2PEvent event = new P2PFileEvent(file, parms);
			listener.onEvent(event);
		}
	}

	public void addListener(P2PListener listener) {
		listeners.add(listener);
	}

	public void remove(P2PListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onEvent(Message message) {
		
		if (message instanceof PingReqMessage) {
			PeerNode node = ((PingReqMessage) message).getPeerNode();
			System.out.println("received req message from " + node.getHost().getPort());
			chord.add(node);
			sendMessage(node.getHost(), new PingRespMessage(peerNode));
		} else if (message instanceof PingRespMessage) {
			PeerNode node = ((PingRespMessage) message).getPeerNode();
			System.out.println("received resp message " + node.getHost().getPort());
			chord.add(node);
		} else {
			for (P2PListener listener : listeners) {
				P2PEvent event = new P2PMessageEvent(message);
				listener.onEvent(event);
			}
		}
	}

	public List<Host> getSeeds() {
		return new ArrayList<Host>(config.getSeeds());
	}
	
	private class SeedFinger implements Runnable {

		@Override
		public void run() {
			System.out.println("checking seed list");
			for (Host host: seeds) {
				sendMessage(host, new PingReqMessage(peerNode));
			}
		}
		
	}
	
	public PeerNode getPeerNode() {
		return peerNode;
	}

	public List<PeerNode> getPeers() {
		return new ArrayList<PeerNode>(chord.peers());
	}
}
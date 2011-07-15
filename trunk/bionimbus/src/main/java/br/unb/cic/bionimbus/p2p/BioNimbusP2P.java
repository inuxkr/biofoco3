package br.unb.cic.bionimbus.p2p;

import java.util.concurrent.CopyOnWriteArraySet;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.p2p.messages.Message;

public class BioNimbusP2P {

	private BioNimbusConfig config;
	private boolean client;
	private boolean master;
	private static final int port = 7171;	
	private final CopyOnWriteArraySet<P2PListener> listeners = new CopyOnWriteArraySet<P2PListener>();
	
	private PeerNode thisNode;
	private PeerNode masterNode;

	public BioNimbusP2P(BioNimbusConfig config) {
		this.config = config;
		client = config.isClient();
	}

	public void start() {
		
		System.out.println("Starting p2p instance on port " + port);
		
		thisNode = new PeerNodeImpl();
		
		masterNode = getMaster();
		
		if (masterNode == null) {
			masterNode = runElectionAlgorithm();			
		}
	}

	private PeerNode runElectionAlgorithm() {
		return thisNode;
	}

	private PeerNode getMaster() {
		return masterNode;
	}

	public boolean isMaster() {
		return thisNode.equals(masterNode);
	}
	
	public void stop() {
		System.out.println("Stopping p2p instance ...");
	}
	
	public void sendMessage(Message message) throws P2PException {
		
	}
	
	public void addListener(P2PListener listener) {
		listeners.add(listener);
	}
	
	public void remove(P2PListener listener) {
		listeners.remove(listener);
	}

}

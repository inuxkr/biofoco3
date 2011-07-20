package br.unb.cic.bionimbus.p2p;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.p2p.messages.GetInfoMessage;
import br.unb.cic.bionimbus.p2p.messages.Message;
import br.unb.cic.bionimbus.p2p.messages.PluginInfoMessage;

public class BioNimbusP2P implements Callable<Boolean> {

	private BioNimbusConfig config;
	private boolean client;
	private boolean master;
	private static final int port = 7171;
	private final CopyOnWriteArraySet<P2PListener> listeners = new CopyOnWriteArraySet<P2PListener>();

	private volatile boolean running = false;
	private final ExecutorService executorService = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder()
					.namingPattern("p2p-%d").build());

	private PeerNode thisNode;
	private PeerNode masterNode;

	public BioNimbusP2P(BioNimbusConfig config) {
		this.config = config;
		client = config.isClient();
	}

	public Boolean call() throws Exception {
		running = true;

		while (running) {
			System.out.println("Running p2p thread...");

			GetInfoMessage msg = new GetInfoMessage();
			P2PEvent event = new P2PEvent();
			event.setMessage(msg);

			for (P2PListener listener : listeners) {
				listener.onEvent(event);
			}

			Thread.sleep(30000);
		}
		return true;
	}

	public void start() {

		System.out.println("Starting p2p instance on port " + port);

		thisNode = new PeerNodeImpl();

		masterNode = getMaster();

		if (masterNode == null) {
			masterNode = runElectionAlgorithm();
		}

		executorService.submit(this);
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
		running = false;
		executorService.shutdownNow();
	}

	public void sendMessage(Message message) throws P2PException {
		switch(message.getType()) {
		case PLUGININFO:
			PluginInfoMessage infoMsg = (PluginInfoMessage) message;
			System.out.println("numCores = " + infoMsg.getInfo().getNumCores());
			
		}
	}

	public void addListener(P2PListener listener) {
		listeners.add(listener);
	}

	public void remove(P2PListener listener) {
		listeners.remove(listener);
	}

}

package br.unb.cic.bionimbus.p2p;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.messaging.MessageListener;
import br.unb.cic.bionimbus.messaging.MessageService;

public class P2PService implements MessageListener {

	private final MessageService msgService = new MessageService();

	private final CopyOnWriteArrayList<P2PListener> listeners = new CopyOnWriteArrayList<P2PListener>();
	
	private final PeerNode peerNode = PeerFactory.createPeer(true);
	
	private final ChordRing chord = new ChordRing(peerNode);

	private final BioNimbusConfig config;
	
	
	public P2PService(BioNimbusConfig config) {
		this.config = config;
	}

	public void start() {
		
		List<Integer> types = new ArrayList<Integer>();

		for (P2PMessageType enumType : P2PMessageType.values())
			types.add(enumType.ordinal());

		msgService.bind(new InetSocketAddress(config.getHost().getAddress(), config.getHost().getPort()));
		msgService.addListener(this, types);
		msgService.start(new P2PMessageFactory());
		
		chord.start();
		
	}

	public void shutdown() {
		msgService.shutdown();
		chord.stop();
	}

	public boolean isMaster() {
		return true;
	}
	
	public void sendMessage(Message message) {
		msgService.sendMessage(new InetSocketAddress("localhost", 9999), message);
	}

	public void sendMessage(Host host, Message message) {
		//TODO: ESSE METODO TEM QUE FUNCIONAR!
		msgService.sendMessage(new InetSocketAddress(host.getAddress(), host.getPort()), message);
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
}

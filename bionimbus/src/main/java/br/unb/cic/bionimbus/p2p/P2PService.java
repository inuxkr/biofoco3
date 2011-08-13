package br.unb.cic.bionimbus.p2p;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.messaging.MessageListener;
import br.unb.cic.bionimbus.messaging.MessageService;

public class P2PService implements MessageListener {

	private final MessageService msgService = new MessageService();

	private final CopyOnWriteArrayList<P2PListener> listeners = new CopyOnWriteArrayList<P2PListener>();

	private BioNimbusConfig config;

	public void start() {
		List<Integer> types = new ArrayList<Integer>();

		for (P2PMessageType enumType : P2PMessageType.values())
			types.add(enumType.ordinal());

		msgService.addListener(this, types);
		msgService.start(new P2PMessageFactory());
	}

	public void shutdown() {
		msgService.shutdown();
	}

	public boolean isMaster() {
		return true;
	}

	public void sendMessage(Message message) {
		msgService.sendMessage(new InetSocketAddress("localhost", 8080),
				message);
	}

	public void addListener(P2PListener listener) {
		listeners.add(listener);
	}

	public void remove(P2PListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onEvent(Message message) {
		for (P2PListener listener : listeners) {
			P2PEvent event = new P2PMessageEvent(message);
			listener.onEvent(event);
		}
	}

	public void setConfig(BioNimbusConfig config) {
		this.config = config;
	}
}

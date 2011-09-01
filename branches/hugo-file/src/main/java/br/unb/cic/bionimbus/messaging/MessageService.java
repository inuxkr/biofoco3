package br.unb.cic.bionimbus.messaging;

import java.net.InetSocketAddress;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MessageService {

	private final MessageServiceServer server = new MessageServiceServer();

	private final MessageServiceClient client = new MessageServiceClient();

	private final Multimap<Integer, MessageListener> listenersMap = HashMultimap.create();
	
	private InetSocketAddress bindSocket;
	
	public void bind(InetSocketAddress bindSocket) {
		this.bindSocket = bindSocket;
	}
	
	public InetSocketAddress getSocket() {
		return bindSocket;
	}

	public void start(MessageFactory messageFactory) {
		server.start(this, messageFactory);
	}

	public void shutdown() {
		server.shutdown();
		client.shutdown();
	}

	public void addListener(MessageListener listener, List<Integer> types) {
		for (Integer type : types)
			listenersMap.put(type, listener);
	}

	public void recvMessage(Message message) {
		for (MessageListener listener : listenersMap.get(message.getType()))
			listener.onEvent(message);
	}

	public void sendMessage(InetSocketAddress addr, Message message) {
		client.sendMessage(addr, message);
	}
}

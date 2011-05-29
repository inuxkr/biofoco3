package br.biofoco.p2p.broker.storage;	

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import br.biofoco.p2p.broker.EventMessage;
import br.biofoco.p2p.resources.PingMessage;

public class MemoryDatastore implements DatastoreService {
	
	public static final long TTL = 3;
	
	private Multimap<String, EventMessage> mailboxes = Multimaps.synchronizedMultimap(HashMultimap.<String,EventMessage>create());
	
	private ConcurrentMap<String, PingMessage> presence = new MapMaker()
																.concurrencyLevel(32)
																.initialCapacity(100)
																.expiration(TTL, TimeUnit.MINUTES)
																.makeMap();

	@Override
	public void addMessage(EventMessage message) {
		mailboxes.put(message.getReceiver(), message);
	}

	@Override
	public Collection<EventMessage> getMessages(String mailbox) {		
		return mailboxes.removeAll(mailbox);
	}

	@Override
	public void ping(PingMessage message) {
		presence.put(message.getId(), message);
	}

	@Override
	public Collection<PingMessage> getPresence() {
		return presence.values();
	}
	
}

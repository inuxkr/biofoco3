package br.biofoco.p2p.broker.storage;

import java.util.Collection;
import br.biofoco.p2p.broker.EventMessage;
import br.biofoco.p2p.resources.PingMessage;


public interface DatastoreService {

	void addMessage(EventMessage message);
	
	void ping(PingMessage message);

	Collection<EventMessage> getMessages(String mailbox);

	Collection<PingMessage> getPresence();

}

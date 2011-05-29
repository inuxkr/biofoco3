package br.biofoco.p2p.resources;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.biofoco.p2p.broker.EventMessage;
import br.biofoco.p2p.broker.JsonUtil;
import br.biofoco.p2p.broker.storage.DataStoreServiceFactory;
import br.biofoco.p2p.broker.storage.DatastoreService;


@Path("/")
public class MailboxResource {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getMessage() {
		return "Webapp Broker";
	}
		
	@POST
	@Path("/mailbox/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String storeMessage(@PathParam("id") String receiver
			                 , @FormParam("sender") String sender
			                 , @FormParam("body") String body){
		
		
		EventMessage message = new EventMessage(sender, receiver, body);
		  
		try {
			DatastoreService datastore = DataStoreServiceFactory.getDatastoreServie();
			datastore.addMessage(message);
		} catch (Exception e) {
			return String.format("{\"code\"=404, message=\"%s\"}", e.getMessage());
		}
		
		return "{\"code\"=220, message=\"Message successfully stored\"}";
        
	}
		
	@GET
	@Path("/mailbox/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String retrieveMessage(@PathParam("id") String receiver) {
		
		try {
			DatastoreService datastore = DataStoreServiceFactory.getDatastoreServie();
			Collection<EventMessage> messages = datastore.getMessages(receiver);
			return JsonUtil.codec(messages);
		} catch (Exception e) {
			return String.format("{\"code\"=404, message=\"%s\"}", e.getMessage());
		}
		
	}	
	
	@POST
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public String ping(@FormParam("id") String id){
		
		PingMessage message = new PingMessage(id, System.currentTimeMillis());
		
		try {
			DatastoreService datastore = DataStoreServiceFactory.getDatastoreServie();
			datastore.ping(message);
		} catch (Exception e) {
			return String.format("{\"code\"=404, message=\"%s\"}", e.getMessage());
		}
		return "{\"code\"=220, message=\"Pong\"}";
	}
	
	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)	
	public String ping() {
				
		try {
			DatastoreService datastore = DataStoreServiceFactory.getDatastoreServie();
			Collection<PingMessage> entities = datastore.getPresence();
			
			return JsonUtil.codec(new ArrayList<PingMessage>(entities));
			
		} catch (Exception e) {
			e.printStackTrace();
			return String.format("{\"code\"=404, message=\"%s\"}", e.getMessage());
		}
	}
}

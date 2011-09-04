package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Charsets;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class StatusRespMessage extends AbstractMessage {
	
	private PluginTask task;
	
	public StatusRespMessage(){
		super();
	}
	
	public StatusRespMessage(PeerNode peer, PluginTask task) {
		super(peer);
		this.task = task;
	}
	
	public PluginTask getPluginTask() {
		return task;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		this.task = mapper.readValue(buffer, PluginTask.class);
		
		ObjectMapper mapper = new ObjectMapper();
		BulkMessage message = mapper.readValue(buffer, BulkMessage.class);
		
		String id = message.getPeerID();
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(message.getHost());
		
		task = message.getTask();
		
	}

	@Override
	public byte[] serialize() throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsBytes(task);
		
		BulkMessage message = new BulkMessage();
		message.setPeerID(peer.getId().toString());
		message.setHost(peer.getHost());
		message.setTask(task);
		
		ObjectMapper mapper = new ObjectMapper();
		String raw = mapper.writeValueAsString(message);
		return raw.getBytes(Charsets.UTF_8);
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSRESP.code();
	}

}

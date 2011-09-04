package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;

import com.google.common.base.Charsets;

public class StatusReqMessage extends AbstractMessage {
	
	private String taskId;
	
	public StatusReqMessage() {
		super();
	}
	
	public StatusReqMessage(PeerNode peer, String taskId) {
		super(peer);
		this.taskId = taskId;
	}
	
	public String getTaskId() {
		return taskId;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
//		this.taskId = new String(buffer);		
		
		ObjectMapper mapper = new ObjectMapper();
		BulkMessage message = mapper.readValue(buffer, BulkMessage.class);
		
		String id = message.getPeerID();
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(message.getHost());
		taskId = message.getTaskId();
	}

	@Override
	public byte[] serialize() throws Exception {
		
//		return taskId.getBytes();
		
		BulkMessage message = new BulkMessage();
		message.setPeerID(peer.getId().toString());
		message.setHost(peer.getHost());
		message.setTaskId(taskId);
		
		ObjectMapper mapper = new ObjectMapper();
		String raw = mapper.writeValueAsString(message);
		return raw.getBytes(Charsets.UTF_8);
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSREQ.code();
	}

}

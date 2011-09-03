package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class StatusReqMessage implements Message {
	
	private String taskId;
	
	public StatusReqMessage() {
	}
	
	public StatusReqMessage(String taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskId() {
		return taskId;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		this.taskId = new String(buffer);
	}

	@Override
	public byte[] serialize() {
		return taskId.getBytes();
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSREQ.ordinal();
	}

}
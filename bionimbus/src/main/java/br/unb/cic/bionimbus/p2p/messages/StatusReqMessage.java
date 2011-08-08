package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class StatusReqMessage implements Message {
	
	private String taskId;
	
	public String getTaskId() {
		return taskId;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSREQ.ordinal();
	}

}

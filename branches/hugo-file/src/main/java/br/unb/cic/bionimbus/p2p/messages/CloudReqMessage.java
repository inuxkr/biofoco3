package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class CloudReqMessage implements Message {
	
	public CloudReqMessage() {
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
	}

	@Override
	public byte[] serialize() throws Exception {
		/* Empty message */
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.CLOUDREQ.ordinal();
	}

}

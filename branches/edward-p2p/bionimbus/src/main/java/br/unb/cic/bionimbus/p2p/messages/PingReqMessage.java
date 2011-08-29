package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class PingReqMessage implements Message {

	@Override
	public byte[] serialize() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType() {
		return P2PMessageType.PINGREQ.ordinal();
	}

}

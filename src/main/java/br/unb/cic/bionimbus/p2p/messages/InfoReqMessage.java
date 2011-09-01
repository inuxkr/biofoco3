package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class InfoReqMessage implements Message {

	@Override
	public void deserialize(byte[] buffer) throws Exception {
	}

	@Override
	public byte[] serialize() {
		/* this message is empty */
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.INFOREQ.ordinal();
	}

}

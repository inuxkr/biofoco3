package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class CloudReqMessage extends AbstractMessage {
	
	@Override
	public int getType() {
		return P2PMessageType.CLOUDREQ.code();
	}
}

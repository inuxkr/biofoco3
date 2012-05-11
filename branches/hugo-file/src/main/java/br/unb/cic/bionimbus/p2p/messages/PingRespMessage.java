package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;

public class PingRespMessage extends AbstractMessage {
	
	public PingRespMessage() {
		super();
	}

	public PingRespMessage(PeerNode peerNode) {
		super(peerNode);
	}
		
	@Override
	public int getType() {
		return P2PMessageType.PINGRESP.code();
	}

}
package br.unb.cic.bionimbus.p2p;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.messaging.MessageFactory;

public class P2PMessageFactory extends MessageFactory {
	
	public P2PMessageFactory() {
		super("p2p");
	}

	@Override
	public Message getMessage(int id, byte[] buffer) {
		return null;
	}

}

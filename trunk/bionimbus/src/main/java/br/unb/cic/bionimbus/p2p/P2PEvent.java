package br.unb.cic.bionimbus.p2p;

import br.unb.cic.bionimbus.p2p.messages.Message;

public class P2PEvent {
	
	private Message msg;
	
	public void setMessage(Message msg) {
		this.msg = msg;
	}

	public Message getMessage() {
		return msg;
	}

}

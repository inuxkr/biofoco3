package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PErrorType;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public abstract class ErrorMessage implements Message {
	
	private String error;
	
	public ErrorMessage(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	public abstract P2PErrorType getErrorType();

	@Override
	public abstract byte[] serialize();

	@Override
	public int getType() {
		return P2PMessageType.ERROR.ordinal();
	}

}

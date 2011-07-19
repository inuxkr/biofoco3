package br.unb.cic.bionimbus.p2p.transport;

public class WireFormatException extends Exception {

	private static final long serialVersionUID = -6595151516186943841L;
	
	public WireFormatException(String message, Throwable t) {
		super(message, t);
	}

}

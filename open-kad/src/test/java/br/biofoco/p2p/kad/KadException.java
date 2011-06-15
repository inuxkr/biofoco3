package br.biofoco.p2p.kad;

public class KadException extends Exception {

	private static final long serialVersionUID = 1L;

	public KadException(Throwable t){
		super(t);
	}
	
	public KadException(String message){
		super(message);
	}
}

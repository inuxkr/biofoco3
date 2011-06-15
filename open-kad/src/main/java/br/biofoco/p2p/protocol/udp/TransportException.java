package br.biofoco.p2p.protocol.udp;

public class TransportException extends Exception {

	private static final long serialVersionUID = -1214563768603594877L;
	
	public TransportException(String message, Throwable throwable){
		super(message, throwable);
	}

}

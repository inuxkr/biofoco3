package br.biofoco.p2p.protocol.udp.event;


public interface Server {
	
	void start() throws TransportException;
	
	void stop() throws TransportException;

}

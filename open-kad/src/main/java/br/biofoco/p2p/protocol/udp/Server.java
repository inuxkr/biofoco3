package br.biofoco.p2p.protocol.udp;


public interface Server {
	
	void start() throws TransportException;
	
	void stop() throws TransportException;

}

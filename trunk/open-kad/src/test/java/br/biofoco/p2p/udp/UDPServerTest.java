package br.biofoco.p2p.udp;

import br.biofoco.p2p.protocol.udp.TransportException;
import br.biofoco.p2p.protocol.udp.UDPServer;


public class UDPServerTest  {
	
	public static void main(String[] args) throws TransportException {
		UDPServer server = new UDPServer();
		server.start();
	}
}

package br.biofoco.p2p.protocol.tcp;

import java.io.IOException;

import br.biofoco.p2p.protocol.tcp.TcpServer;

public class TcpServerTest {

	public static void main(String[] args) throws IOException {
		TcpServer server = new TcpServer();
		server.start();
	}
}

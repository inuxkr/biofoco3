package br.biofoco.p2p.protocol.tcp;

import java.io.IOException;

import br.biofoco.p2p.protocol.tcp.TcpClient;

public class TcpClientTest {

	public static void main(String[] args) throws IOException {
		TcpClient client = new TcpClient();
		
		System.out.println(client.send("localhost", 2121, "Hello, world!"));
	}
}

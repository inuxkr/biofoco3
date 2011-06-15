package br.biofoco.p2p.udp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import br.biofoco.p2p.protocol.udp.UDPClient;
import br.biofoco.p2p.protocol.udp.UDPServer;

public class UDPClientTest {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		UDPClient client = new UDPClient();
		
		List<String> list = new ArrayList<String>();
		list.add("RESPOSTA: " + client.send("127.0.0.1", UDPServer.UDP_PORT, "PING") + "");
		list.add("RESPOSTA: " + client.send("127.0.0.1", UDPServer.UDP_PORT, "TIME") + "");
		list.add("RESPOSTA: " + client.send("127.0.0.1", UDPServer.UDP_PORT, "ECHO Hello World") + "");
		
		System.out.println(list.size());				
		for (String e : list) {
			System.out.println(e);
		}
	
	}
}

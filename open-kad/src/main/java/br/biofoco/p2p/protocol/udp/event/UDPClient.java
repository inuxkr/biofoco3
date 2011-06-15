
package br.biofoco.p2p.protocol.udp.event;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.common.base.Charsets;

public class UDPClient {
	
	public static final int TIMEOUT = 30000; // in miliseconds
	
	public String send(String address, int port, String message) throws IOException {		
		InetAddress server = InetAddress.getByName(address);
		return requestResponse(server, port, message.getBytes());
	}
	
	
	public void oneWay(InetAddress server, int port, byte[] data) throws IOException {
		System.out.println("Sending message to " + server + " on port " + port);
		
		//Request
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(TIMEOUT);
		DatagramPacket request = new DatagramPacket(data, data.length, server, port);		
		socket.send(request);
	}

	public String requestResponse(InetAddress server, int port, byte[] data) throws IOException {
				
		//Request
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(TIMEOUT);
		DatagramPacket request = new DatagramPacket(data, data.length, server, port);
		
		socket.send(request);		
		
		socket.setSoTimeout(TIMEOUT);
		
		byte[] buf = new byte[UDPServer.MAX_PACKET_SIZE];
		DatagramPacket response = new DatagramPacket(buf, buf.length, socket.getLocalAddress(), socket.getLocalPort());
		socket.setSoTimeout(TIMEOUT);
		socket.receive(response);
		
		socket.close();
		
		return new String(response.getData(), 0, response.getLength(), Charsets.UTF_8);
	}	
}

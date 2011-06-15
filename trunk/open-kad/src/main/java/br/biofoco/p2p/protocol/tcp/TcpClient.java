package br.biofoco.p2p.protocol.tcp;

import java.io.IOException;
import java.net.Socket;

public class TcpClient {
	
	public String send(String host, int port, String message) throws IOException {				
		Socket socket = new Socket(host, port);
		UTFUtils.writeString(socket.getOutputStream(), message);		
		return UTFUtils.readString(socket.getInputStream());
	}
}

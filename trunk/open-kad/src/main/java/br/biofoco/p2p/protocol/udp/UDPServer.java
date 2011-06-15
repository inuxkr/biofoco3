package br.biofoco.p2p.protocol.udp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.biofoco.p2p.protocol.tcp.UTFUtils;

import com.google.common.base.Charsets;


public class UDPServer implements Server {

	private DatagramSocket server;
	private volatile boolean running = false;
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPServer.class);
	
	public static final int UDP_PORT = 2191;
	public static final int MAX_PACKET_SIZE = 65507;

	public void start() throws TransportException {

		try {
			initServerLoop();

		} catch (SocketException e) {
			throw new TransportException("Cannot start server", e);
		}

	}

	private void initServerLoop() throws SocketException {
		LOGGER.debug("Starting UDP server on port " + UDP_PORT);

		server = new DatagramSocket(UDP_PORT);

		running = true;

		while (running) {

			byte[] buffer = new byte[MAX_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			try {
				server.receive(packet);
				LOGGER.debug("udp packet received!");
				executor.submit(new ServerHandler(packet));

			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}

		}

		LOGGER.debug("UDP server stopped");
	}

	public void stop() {
		running = false;
		server.close();
		executor.shutdownNow();
	}

	private class ServerHandler implements Runnable {

		private final DatagramPacket packet;

		public ServerHandler(DatagramPacket packet) {
			this.packet = packet;
		}

		@Override
		public void run() {

			try {
				
				byte[] buf = packet.getData();
				
//				String request = UTFUtils.readString(new ByteArrayInputStream(buf, 0, buf.length));				
				String request = new String(packet.getData(), 0, packet.getLength(), Charsets.UTF_8);
				
				System.out.println(request);
				
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				
				String response = doProcess(request);
				
				UDPClient client = new UDPClient();
				client.oneWay(address, port, response.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public String doProcess(String request) {
			if (request.startsWith("PING")) {
				return "PONG";
			}
			if (request.startsWith("TIME")){
				return new Date().toString();
			}
			if (request.startsWith("ECHO")) {
				return request.substring(request.indexOf(" ")).trim();
			}
			
			return "undefined command";
		}
	}
}

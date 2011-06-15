package br.biofoco.p2p.protocol.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

	private ServerSocket server;
	private static final int DEFAULT_PORT = 2121;
	
	private volatile boolean running = false;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
		
	public void start() throws IOException {
		server = new ServerSocket(DEFAULT_PORT);
			
		running = true;
		
		while (running) {
			Socket client = server.accept();
			executor.execute(new ConnectionHandler(client));
		}
	}
	
	public void stop() throws IOException {
		running = false;
		executor.shutdown();
		server.close();
	}
	
	private class ConnectionHandler implements Runnable {
		
		private final Socket client;

		public ConnectionHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {						
			try {
				String request = UTFUtils.readString(client.getInputStream());
				UTFUtils.writeString(client.getOutputStream(), request);
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				closeConnection(client);
			}			
		}

		private void closeConnection(Socket client) {
			try {
				client.close();
			}
			catch (IOException ie) {
				ie.printStackTrace();
			}
		}

	
	}
}

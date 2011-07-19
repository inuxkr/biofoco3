package br.unb.cic.bionimbus.p2p.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.unb.cic.bionimbus.p2p.dht.UTFUtils;

public class TcpTransport implements Transport {

	private static final Logger LOG = LoggerFactory.getLogger(TcpTransport.class);
	
	public static final int TIMEOUT = 30000;
	public static final int DEFAULT_PORT = 9191;

	private ServerSocket server;
	private int port;

	private volatile boolean running = false;

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private TransportHandler transportHandler;

	TcpTransport() {
		this(DEFAULT_PORT);
	}

	public TcpTransport(int port) {
		this.port = port;
	}

	public void addHandler(TransportHandler handler) {
		this.transportHandler = handler;
	}

	public void start() throws IOException {

		server = new ServerSocket(port);
		
		running = true;

		executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					LOG.debug("TCP channel listening on port " + port);
					while (running) {
						Socket client = server.accept();
						executor.execute(new ConnectionHandler(client));
					}
					LOG.debug("TCP channel closed");
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
				finally {
					try {
						server.close();
					} catch (IOException ignore) {
						LOG.error("Error during closing");
					}
				}
			}

		});
	}

	public void stop() throws IOException {
		running = false;
		executor.shutdownNow();
		server.close();
	}

	private class ConnectionHandler implements Runnable {

		private final Socket client;

		public ConnectionHandler(Socket client) {
			LOG.debug("connection stablished from client ".concat(client.getInetAddress().toString()));
			this.client = client;
		}

		@Override
		public void run() {
			try {
				if (transportHandler != null) {
					String input = UTFUtils.readString(client.getInputStream());
					WireMessage request = WireFormat.deserialize(input);
					
					WireMessage response = transportHandler.doRequestResponse(request);
					String output = WireFormat.serialize(response);
					UTFUtils.writeString(client.getOutputStream(), output);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (WireFormatException e) {
				e.printStackTrace();
			} finally {
				closeConnection(client);
			}
		}

		private void closeConnection(Socket client) {
			try {
				client.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

	public WireMessage sendMessage(String host, int port, WireMessage message) throws IOException, WireFormatException {

		Socket socket = new Socket(host, port);
		socket.setSoTimeout(TIMEOUT);
		
		String input = WireFormat.serialize(message);

		UTFUtils.writeString(socket.getOutputStream(), input);
		
		String output = UTFUtils.readString(socket.getInputStream());
		
		return WireFormat.deserialize(output);
	}
}

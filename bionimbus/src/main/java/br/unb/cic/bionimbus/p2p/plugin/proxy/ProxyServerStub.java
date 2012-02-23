package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProxyServerStub implements Proxy {

	private volatile boolean running;

	private ServerSocket server;
	private final ExecutorService executorService;

	private volatile boolean clientConnection = false;

	private final Queue<String> outgoingQueue = new ConcurrentLinkedQueue<String>();
	private final Map<String, BlockingQueue<String>> incomingQueue = new HashMap<String, BlockingQueue<String>>();
	
	private static final Logger LOG = LoggerFactory.getLogger(ProxyServerStub.class);

	private final int port;

	public ProxyServerStub(ExecutorService executor) {
		this(executor, PORT);
	}

	public ProxyServerStub(ExecutorService executor, int port) {
		this.port = port;
		this.executorService = executor;
		
		for (String command : Arrays.asList("SAVE-FILE", "INFO", "GET-FILE", "RUN-TASK")) {
			incomingQueue.put(command, new LinkedBlockingQueue<String>());
		}
		
	}

	public void request(String command) {
		outgoingQueue.add(command);
	}

	public void start() {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				
				System.out.println("Starting ProxyServerStub on port " + port);

				try {
					server = new ServerSocket(port);
					server.setReuseAddress(true);
					running = true;
					while (running) {
						// if (!clientConnection) {
						System.out.println("Listening to connections...");
						Socket client = server.accept();
						clientConnection = true;
						executorService.execute(new ClientConnection(client));
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
					running = false;
					clientConnection = false;
				}
			}

		});

	}

	public void shutdown() {
		try {
			System.out.println("Stopping ProxyServerStub ...");
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			running = false;
			clientConnection = false;
		}
	}

	private final class ClientConnection implements Runnable {

		private final Socket client;

		public ClientConnection(Socket client) {
			this.client = client;
		}

		public void run() {

			System.out.println("client connected from address " + client.getInetAddress());

			try {

				String command = null;

				while ((command = outgoingQueue.poll()) == null) {
					System.out.print(".");
					TimeUnit.SECONDS.sleep(2);
				}

				do {
					System.out.println("Consumindo dados da fila de entrada...");
					
					DataOutputStream output = new DataOutputStream(client.getOutputStream());
					output.writeUTF(command);

					DataInputStream input = new DataInputStream(client.getInputStream());
					
					String result = input.readUTF();					
					System.out.println("Dados recebidos do cliente: " + result);
					String[] splits = result.split("#");
					command = splits[0];
					String response = splits[1];
					incomingQueue.get(command).add(response);

				} while ((command = outgoingQueue.poll()) != null);
				
				System.out.println("Fila de entrada vazia");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("Fechando socket do stub e ignorando erros");
				closeAndIgnoreErrors(client);
				clientConnection = false;
			}
		}

		private void closeAndIgnoreErrors(Socket socket) {
			try {
				socket.close();
			} catch (IOException ie) { /* ignore */
			}
		}
	}

	public boolean hasClientConnection() {
		return clientConnection;
	}

	public String response(String command) throws InterruptedException {
		System.out.println("Getting response for command " + command);
		return incomingQueue.get(command).take();
	}

}
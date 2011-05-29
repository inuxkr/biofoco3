/**
 * Copyright (C) 2011 University of Brasilia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.biofoco.p2p.transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastTransport {

	private volatile boolean running = false;
	private static final String M_ADDRESS = "235.255.0.1";
	private static final int BUFFER_LENGTH = 65507;
	private static final int PORT = 5000;
	
	private MulticastListener listener;
	
	private MulticastSocket socket;
	private Thread t;
	
	public void start() throws IOException {

		final byte[] b = new byte[BUFFER_LENGTH];
		final DatagramPacket dgram = new DatagramPacket(b, b.length);
		socket = new MulticastSocket(PORT);
		socket.joinGroup(InetAddress.getByName(M_ADDRESS));
		
		running = true;

		t = new Thread() {
			public void run() {
				while (running) {
					try {
						socket.receive(dgram); // block until a datagram is received
						
						if (listener != null) {
							listener.onMessage(new String(dgram.getData(), 0, dgram.getLength()), dgram.getAddress());
						}
					
						dgram.setLength(b.length); // must reset length field!
						
					} catch (IOException e) {
						e.printStackTrace();
						running = false;
					}
				}
			}
		};
		
		t.start();
	}

	public void send(String message) throws IOException {

		 MulticastSocket s = new MulticastSocket();
		 byte buf[] = message.getBytes();
		 DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(M_ADDRESS), PORT);
		 s.send(pack);
		 s.close();
	}

	public void stop() throws IOException {		
		running = false;
		socket.close(); // equivale ao interrupt() para o caso de I/O
	}

	public void setListener(MulticastListener listener) {
		this.listener = listener;
	}

}

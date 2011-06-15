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

package br.biofoco.p2p.core;

import java.net.InetAddress;

import br.biofoco.p2p.transport.HttpTransportServer;
import br.biofoco.p2p.transport.MulticastListener;

public class Messenger implements MulticastListener {

	private final HttpTransportServer httpTransportServer;
//	private final MulticastTransport multicastTransport;
	
	private int httpPort;
	
	public Messenger(int httpPort){
		this.httpPort = httpPort;
		httpTransportServer = new HttpTransportServer(httpPort);
//		multicastTransport = new MulticastTransport();
	}
	
	public void start() throws Exception {
		
//		multicastTransport.setListener(this);
		
//		multicastTransport.start();
//		Thread t = new Thread(new MulticastAnnouncer());
//		t.start();
		
		httpTransportServer.start();
	}

	public void stop() throws Exception {
//		multicastTransport.stop();
		httpTransportServer.stop();
	}
	
//	public class MulticastAnnouncer implements Runnable {
//
//		@Override
//		public void run() {
//			int i = 1;
//			while (true) {
//				try {
//					multicastTransport.send(i + " af443c293205621100:" + httpPort);
//					i++;
//					Thread.sleep(5 * 1000);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//		}
//		
//	}

	@Override
	public void onMessage(String message, InetAddress address) {
		System.out.println("-------------------------------");
		System.out.println(message);
		System.out.println("Received " + message.length() + " bytes from " + address.getHostAddress());
		System.out.println("-------------------------------");
		
	}
}

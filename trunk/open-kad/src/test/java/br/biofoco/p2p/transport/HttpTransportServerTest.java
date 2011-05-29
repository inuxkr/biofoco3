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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class HttpTransportServerTest {
	
	private static HttpTransportServer server;

	public static void main(String[] args) throws Exception {
				
		server = new HttpTransportServer(9090);
		
		new Thread(new Runnable() {
			
			public void run() {
				call();
			}
		}).start();
		
		server.start();

	}
	
	public static void call() {
		
		while (!server.isRunning()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Client c = Client.create();
		WebResource r = c.resource("http://localhost:" + 9090);
		
		System.out.println(r.get(String.class)); //AssertEquals		

		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

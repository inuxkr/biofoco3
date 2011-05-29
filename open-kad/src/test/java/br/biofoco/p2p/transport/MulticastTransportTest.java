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
import java.math.BigInteger;
import java.net.InetAddress;

import br.biofoco.p2p.transport.MulticastListener;
import br.biofoco.p2p.transport.MulticastTransport;

public class MulticastTransportTest {

	public static void main(String[] args) throws IOException {
		
		MulticastTransport transport = new MulticastTransport();
		
		transport.setListener(new TestListener());
		
		transport.start();
		
		for (int i = 0; i < 10; i++){
			String texto = new BigInteger("1975").toString(16) + ":" + 9090;
			transport.send(texto);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		transport.stop();
	}
	
	public static class TestListener implements MulticastListener {

		@Override
		public void onMessage(String string, InetAddress address) {
			System.out.println(string);
		}
		
	}
}

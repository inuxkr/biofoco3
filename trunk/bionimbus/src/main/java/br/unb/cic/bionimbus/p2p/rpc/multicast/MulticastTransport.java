/**
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package br.unb.cic.bionimbus.p2p.rpc.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;
import org.jboss.netty.util.CharsetUtil;

import br.unb.cic.bionimbus.p2p.transport.TransportHandler;
import br.unb.cic.bionimbus.p2p.transport.WireFormat;
import br.unb.cic.bionimbus.p2p.transport.WireFormatException;
import br.unb.cic.bionimbus.p2p.transport.WireMessage;

public class MulticastTransport  {
	
	private MulticastSocket socket;
	private String address;
	private volatile boolean running = false;
	
	private int port;
	
	public MulticastTransport(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	private static final Logger LOGGER = Logger.getLogger(MulticastTransport.class);
	private TransportHandler transportHandler;
	
	public void addListener(TransportHandler handler) {
		transportHandler = handler;
	}
	
	public void start() throws IOException {
		
		socket = new MulticastSocket(port);
        socket.joinGroup(InetAddress.getByName(address));
        
        Thread t = new Thread(new ServerListener());
        t.start();
        
        LOGGER.debug("Multicast service started!");
        
        running = true;
	}
		
	public void sendMessage(String message) throws IOException {
		LOGGER.debug("Sending multicast message");
		byte[] buffer = new byte[65535];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);		
		socket.send(packet);
	}
	
	private class ServerListener implements Runnable {

		@Override
		public void run() {
			LOGGER.debug("Starting multicast listener on port " + port);
			try {
				while (running) {
					byte[] buffer = new byte[65535];
					DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
					socket.receive(packet);
					LOGGER.debug("Multicast message received");
					
					if (transportHandler != null) {
						
						String input = new String(packet.getData(), CharsetUtil.UTF_8);
						WireMessage request = WireFormat.deserialize(input);						
						transportHandler.doRequestResponse(request);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WireFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
	}

}

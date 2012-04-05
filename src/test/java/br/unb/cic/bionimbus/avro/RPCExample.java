package br.unb.cic.bionimbus.avro;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import br.unb.cic.bionimbus.avro.proto.PingPong;

/**
 * http://avro.apache.org/docs/1.6.2/idl.html
 * 
 * @author edward
 * 
 */
public class RPCExample {
	private static NettyServer server;

	// Implementation of the protocol
	public static class PingPongImpl implements PingPong {

		public String ping(CharSequence message) throws AvroRemoteException {
			return "Hello World " + message;
		}
	}

	public static void main(String[] args) throws IOException {

		// SERVER
		server = new NettyServer(new SpecificResponder(PingPong.class,
				new PingPongImpl()), new InetSocketAddress(9191));

		// CLIENT
		NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(
				server.getPort()));
		PingPong proxy = (PingPong) SpecificRequestor.getClient(PingPong.class,
				client);

		// RPC
		System.out.println("Result: " + proxy.ping("Avro"));

		client.close();
		server.close();
	}
}

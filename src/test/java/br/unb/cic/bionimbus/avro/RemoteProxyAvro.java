package br.unb.cic.bionimbus.avro;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import br.unb.cic.bionimbus.avro.proto.InfoRecord;
import br.unb.cic.bionimbus.avro.proto.NodeState;
import br.unb.cic.bionimbus.avro.proto.PingPong;
import br.unb.cic.bionimbus.avro.proto.RemoteProxyProtocol;
import br.unb.cic.bionimbus.avro.proto.State;
import br.unb.cic.bionimbus.avro.proto.Task;

public class RemoteProxyAvro {

	
	private NettyServer server;

	void start() throws IOException {
		// SERVER
		server = new NettyServer(new SpecificResponder(RemoteProxyProtocol.class, new RemoteProxyProtocolImpl()), new InetSocketAddress(9191));

		// CLIENT
		NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(server.getPort()));
		RemoteProxyProtocol proxy = (RemoteProxyProtocol) SpecificRequestor.getClient(RemoteProxyProtocol.class,	client);

		// RPC
//		System.out.println("Result: " + proxy.getInfo().toString());
		
		NodeState state = new NodeState();
		state.setState(State.IDLE);
		proxy.ping(state);

		client.close();
		server.close();
	}
	
	public static void main(String[] args) throws IOException {
		new RemoteProxyAvro().start();
	}
	
	static class RemoteProxyProtocolImpl implements RemoteProxyProtocol {

		@Override
		public List<Task> pullPendingTasks() throws AvroRemoteException {
			return Collections.emptyList();
		}

		@Override
		public Void ping(NodeState state) throws AvroRemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Void pushNodeInfo(InfoRecord info) throws AvroRemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Void pushCompletedTask(Task task) throws AvroRemoteException {
			// TODO Auto-generated method stub
			return null;
		}

	
	}
}

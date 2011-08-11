package br.unb.cic.bionimbus.p2p.rpc;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

public class UdpServer {
	
	private static final int PORT = 9090;
	
	private ChannelFactory factory;
	private ConnectionlessBootstrap bootstrap;
	
	void start() {
		factory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
		bootstrap = new ConnectionlessBootstrap(factory);
		
		bootstrap.setPipelineFactory(new JsonUdpServerPipelineFactory());
		
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("broadcast", "false");
		bootstrap.setOption("sendBufferSize", 65536);
		bootstrap.setOption("receiveBufferSize", 65536);
		
		System.out.println("Listening on port " + PORT);
		
		bootstrap.bind(new InetSocketAddress(PORT));		
	}

	public static void main(String[] args) {
		new UdpServer().start();
	}
}

package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class UdpServer implements Server {
	
	private int port = 8080;
	private DatagramChannelFactory factory;
	private ConnectionlessBootstrap bootstrap;
	
	public UdpServer() {
		factory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
		bootstrap = new ConnectionlessBootstrap(factory);
	}

	public static void main(String[] args) {
		new UdpServer().start();
	}
	
	public void start() {

		ChannelPipeline p = bootstrap.getPipeline();
		p.addLast("encoder", new StringEncoder());
		p.addLast("decoder", new StringDecoder());
//		p.addLast("logic",   this);

		bootstrap.bind(new InetSocketAddress(port));
	}
	
	public void stop() {
		bootstrap.releaseExternalResources();
	}
}

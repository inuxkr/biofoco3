package br.biofoco.p2p.protocol.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class UdpServer {

	public static void main(String[] args) {
		DatagramChannelFactory f = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
		ConnectionlessBootstrap b = new ConnectionlessBootstrap(f);

		ChannelPipeline p = b.getPipeline();
		p.addLast("encoder", new StringEncoder());
		p.addLast("decoder", new StringDecoder());
//		p.addLast("logic",   this);

		b.bind(new InetSocketAddress(8080));
	}
}

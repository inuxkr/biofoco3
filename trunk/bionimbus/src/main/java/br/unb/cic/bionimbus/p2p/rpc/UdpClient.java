package br.unb.cic.bionimbus.p2p.rpc;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

public class UdpClient {
	
	private static final int PORT = 9090;
	
	private static ChannelFactory factory;
	private static ConnectionlessBootstrap bootstrap;

	public static void main(String[] args) {
		
		factory = new NioDatagramChannelFactory(Executors.newCachedThreadPool());
		bootstrap = new ConnectionlessBootstrap(factory);
		
		bootstrap.setPipelineFactory(new JsonUdpClientPipelineFactory());
		
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("broadcast", "false");
		bootstrap.setOption("sendBufferSize", 65536);
		bootstrap.setOption("receiveBufferSize", 65536);
		
		DatagramChannel c = (DatagramChannel) bootstrap.bind(new InetSocketAddress(0));
		
		WireMessage msg = new WireMessage();
		msg.setId(13);
		
		msg.setData("Hello Netty");

		ChannelFuture future = c.write(msg, new InetSocketAddress("localhost", PORT));
		
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture cf) throws Exception {
				cf.getChannel().close();
				factory.releaseExternalResources();
			}			
		});
		

//        if (!c.getCloseFuture().awaitUninterruptibly(5000)) {
//            System.err.println("QOTM request timed out.");
//            c.close().awaitUninterruptibly();
//        }

//        factory.releaseExternalResources();		
	}
}

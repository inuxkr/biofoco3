package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TcpServer implements Server {
	
	private ServerBootstrap bootstrap;
	private int port = 8080;
	
	public TcpServer() {
		ChannelFactory factory = new NioServerSocketChannelFactory( Executors.newCachedThreadPool(), Executors.newCachedThreadPool() );
		bootstrap = new ServerBootstrap(factory);
	}
	
	public static void main(String[] args) {
	 

	}
	
	public void start() {
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ServerHandler());
			}
		});		

		bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(port));
	}
	
	public void stop() {
		
	}

}

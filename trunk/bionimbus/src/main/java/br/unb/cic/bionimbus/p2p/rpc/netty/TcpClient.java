package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class TcpClient {

	private static int port = 8080;
	private ClientBootstrap bootstrap;

	public static void main(String[] args) {
		
		new TcpClient().connect("localhost", port);

	}
	
	public void connect(String host, int port) {
		
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap (factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new TcpClientHandler());
            }
        });
        
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("connectTimeoutMillis", 5000);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        Channel channel = future.awaitUninterruptibly().getChannel();

        // Wait until the connection is closed or the connection attempt fails.
        channel.getCloseFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                new Thread(new Runnable() {
                    public void run() {
                        // Shut down thread pools to exit
                        // (cannot be executed in the same thread pool!
                        bootstrap.releaseExternalResources();

                        System.out.println("Shutting down");
                    }
                }).start();
            }
        });
	}
}

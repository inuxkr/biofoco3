package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

public class TcpClient {

	private static int port = 8080;
	private ClientBootstrap bootstrap;

	public static void main(String[] args) {
		new TcpClient().sendMessage("localhost", port, "Hello, Netty!");
	}
	
	public void sendMessage(String host, int port, String message) {
		
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap (factory);
        
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new LengthFieldBasedFrameDecoder(65536, 0, 2),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(CharsetUtil.UTF_8),
                        new MyClientHandler());
            }
        });
        
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("connectTimeoutMillis", 5000);
        
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }
        
//        channel.write(message + "\r\n");

        // Wait until the connection is closed or the connection attempt fails.
//        channel.getCloseFuture().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                new Thread(new Runnable() {
//                    public void run() {
//                        // Shut down thread pools to exit
//                        // (cannot be executed in the same thread pool!
//                        bootstrap.releaseExternalResources();
//
//                        System.out.println("Shutting down");
//                    }
//                }).start();
//            }
//        });
        
        // Wait until all messages are flushed before closing the channel.
//        if (lastWriteFuture != null) {
//            lastWriteFuture.awaitUninterruptibly();
//        }       
        
        //close connection
        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
	}
}

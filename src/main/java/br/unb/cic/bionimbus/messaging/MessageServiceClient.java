package br.unb.cic.bionimbus.messaging;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class MessageServiceClient {

	private final ChannelFactory factory = new NioClientSocketChannelFactory(
			Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

	private final ChannelGroup channelGroup = new DefaultChannelGroup("msg-client");

	public void shutdown() {
		ChannelGroupFuture f = channelGroup.close();
		f.awaitUninterruptibly();
		factory.releaseExternalResources();
	}

	public void sendMessage(InetSocketAddress addr, Message message) {
		ClientBootstrap client = new ClientBootstrap(factory);
		client.setPipelineFactory(new MessageServiceClientPipelineFactory(message, channelGroup));
		client.connect(addr);
	}

}

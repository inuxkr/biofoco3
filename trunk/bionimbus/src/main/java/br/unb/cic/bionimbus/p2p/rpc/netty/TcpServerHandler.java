package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerHandler.class);

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		SocketAddress remoteAddress = ctx.getChannel().getRemoteAddress();
		LOGGER.debug("connection received from " + remoteAddress.toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
	    Channel ch = e.getChannel();
	    ch.write(e.getMessage());
	    
	    String message = NettyUtils.readString((ChannelBuffer) e.getMessage());
	    
	    LOGGER.debug(message);
	    
	    if ("quit".equalsIgnoreCase(message.trim())){
	    	ch.close();
	    }
	    
	}

}

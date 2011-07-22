package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientHandler extends SimpleChannelHandler {
	
		private static final Logger LOGGER = LoggerFactory.getLogger(TcpClientHandler.class);

	    @Override
	    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	    	
			SocketAddress remoteAddress = ctx.getChannel().getRemoteAddress();
			
			LOGGER.debug("connection received from " + remoteAddress.toString());
	    	
	        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
	        	        
	        e.getChannel().close();
	    }
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	    	LOGGER.error(e.getCause().getMessage());	    
	        e.getChannel().close();
	    }
}

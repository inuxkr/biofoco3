package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TcpClientHandler extends SimpleChannelHandler {

	    @Override
	    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	    	
			SocketAddress remoteAddress = ctx.getChannel().getRemoteAddress();
			
			System.out.println("connection received from " + remoteAddress.toString());
	    	
	        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
	        	        
	        e.getChannel().close();
	    }
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	        e.getCause().printStackTrace();
	        e.getChannel().close();
	    }
}

package br.unb.cic.bionimbus.p2p.rpc.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TcpClientHandler extends SimpleChannelHandler {

	    @Override
	    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
	        	        
	        e.getChannel().close();
	    }
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	        e.getCause().printStackTrace();
	        e.getChannel().close();
	    }
}

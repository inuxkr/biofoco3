package br.biofoco.p2p.protocol.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TcpServerHandler extends SimpleChannelHandler {

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		System.out.println("connection received!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
//		while (buf.readable()) {
//			System.out.println((char) buf.readByte());
//			System.out.flush();
//		}
		
	    Channel ch = e.getChannel();
	    ch.write(e.getMessage());
	    
	    String message = NettyUtils.readString((ChannelBuffer) e.getMessage());
	    
	    System.out.println(message);
	    
	    if ("quit".equalsIgnoreCase(message.trim())){
	    	ch.close();
	    }
	    
	}

}

package br.unb.cic.bionimbus.p2p.rpc.netty;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class MyClientHandler extends SimpleChannelUpstreamHandler {

	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
//		   logger.log(Level.INFO, e.getMessage().toString());
		    byte[] message = null;
			try {
				message = "Hello over there!".getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		    ChannelBuffer buf = ChannelBuffers.buffer(message.length + 2);
		    buf.clear();
		    short len = (short)message.length;
		    buf.writeShort(len);
		    buf.writeBytes(message);
		    e.getChannel().write(buf);

//		    if(!sent){
//		        //do something and set sent
//		    }
    }
}

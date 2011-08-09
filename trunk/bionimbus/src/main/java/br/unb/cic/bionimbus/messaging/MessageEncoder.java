package br.unb.cic.bionimbus.messaging;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class MessageEncoder extends SimpleChannelHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Message message = (Message) e.getMessage();
		byte[] encoded = message.serialize();
		
		ChannelBuffer buffer = ChannelBuffers.buffer(8 + encoded.length);
		buffer.writeInt(encoded.length);
		buffer.writeInt(message.getType());
		buffer.writeBytes(encoded);
	}

}

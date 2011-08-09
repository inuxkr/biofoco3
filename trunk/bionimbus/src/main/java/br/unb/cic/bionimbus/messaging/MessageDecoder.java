package br.unb.cic.bionimbus.messaging;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class MessageDecoder extends FrameDecoder {

	private final MessageFactory factory;

	public MessageDecoder(MessageFactory factory) {
		this.factory = factory;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < 8)
			return null;

		buffer.markReaderIndex();

		int length = buffer.readInt();
		int type = buffer.readInt();

		if (buffer.readableBytes() < length) {
			buffer.resetReaderIndex();
			return null;
		}

		byte[] decoded = new byte[length];
		buffer.readBytes(decoded);

		return factory.getMessage(type, decoded);
	}

}

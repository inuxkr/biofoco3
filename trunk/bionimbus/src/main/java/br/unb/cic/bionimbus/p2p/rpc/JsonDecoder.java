package br.unb.cic.bionimbus.p2p.rpc;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class JsonDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {

		byte[] decoded = new byte[buffer.readableBytes()];
		buffer.readBytes(decoded);

		return deserialize(decoded);
	}

	private Object deserialize(byte[] buf) throws CorruptedFrameException {
		
		ObjectMapper mapper = new ObjectMapper();

		Throwable t;
		try {
			return mapper.readValue(buf, WireMessage.class);
		} catch (JsonParseException e) {
			t = e;
		} catch (JsonMappingException e) {
			t = e;
		} catch (IOException e) {
			t = e;
		}

		throw new CorruptedFrameException("Error deserializing json: " + t.getMessage());

	}

}

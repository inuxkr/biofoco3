package br.unb.cic.bionimbus.messaging;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

public class MessageServiceFileClientPipelineFactory implements
		ChannelPipelineFactory {

	private final String fileName;
	
	private boolean isGet = false;
	
	private MessageServiceClient client;

	public MessageServiceFileClientPipelineFactory(String fileName) {
		this.fileName = fileName;
	}
	
	public MessageServiceFileClientPipelineFactory(String fileName, boolean isGet, MessageServiceClient client) {
		this.fileName = fileName;
		this.isGet = isGet;
		this.client = client;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("codec", new HttpClientCodec());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("handler", new MessageServiceFileClientHandler(fileName, isGet, client));

		return pipeline;
	}

}

package br.unb.cic.bionimbus.messaging;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

public class MessageServiceFileClientPipelineFactory implements
		ChannelPipelineFactory {

	private String fileName;
	
	private boolean isGet = false;

	public MessageServiceFileClientPipelineFactory(String fileName) {
		this.fileName = fileName;
	}
	
	public MessageServiceFileClientPipelineFactory(String fileName, boolean isGet) {
		this.fileName = fileName;
		this.isGet = isGet;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("codec", new HttpClientCodec());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("handler", new MessageServiceFileClientHandler(fileName, isGet));

		return pipeline;
	}

}

package br.biofoco.p2p.protocol;

import org.junit.Test;

import br.biofoco.p2p.protocol.JsonCodec;
import br.biofoco.p2p.protocol.Message;
import br.biofoco.p2p.protocol.MessageBuilder;
import br.biofoco.p2p.protocol.MessageType;

public class CodecTest {

	@Test
	public void testEncoding() {
		
		Message msg = MessageBuilder.start()
		                                .id()
		                                .sender(1L)
		                                .receiver(1L)
		                                .type(MessageType.REQUEST)
		                                .command("PING")
		                                .data("Hello".getBytes())
		                                .build();
		                                
		String value = JsonCodec.toJSON(msg);
		Message resp = JsonCodec.fromJSON(value);
		System.out.println(resp.toString());
		
		System.out.println(new String(resp.getData()));
		
	}
	
}

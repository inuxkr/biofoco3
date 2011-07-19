package br.unb.cic.bionimbus.p2p.transport;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public final class WireFormat {
	
	private WireFormat() {}

	public static WireMessage deserialize(String input) throws WireFormatException {
		ObjectMapper mapper = new ObjectMapper();
		Throwable t;
		try {
			return mapper.readValue(input, WireMessage.class);
		} catch (JsonParseException e) {
			t = e.getCause();
			
		} catch (JsonMappingException e) {
			t = e.getCause();
		} catch (IOException e) {
			t = e.getCause();
		}
		
		throw new WireFormatException("Error during message decoding!", t);
	}
	
	public static String serialize(WireMessage message) throws WireFormatException {
		
		ObjectMapper mapper = new ObjectMapper();
		Throwable t;
		try {
			return mapper.writeValueAsString(message);
		} catch (JsonGenerationException e) {
			t = e.getCause();
		}catch (JsonMappingException e) {
			t = e.getCause();
		} catch (IOException e) {
			t = e.getCause();
		}
		
		throw new WireFormatException("Error during message decoding!", t);
		
	}

}

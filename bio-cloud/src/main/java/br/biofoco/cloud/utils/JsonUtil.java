package br.biofoco.cloud.utils;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public final class JsonUtil {

	public static String toString(Object value) throws IOException {
		ObjectMapper mapper = new ObjectMapper();		
		return mapper.writeValueAsString(value); 
	}
	
}

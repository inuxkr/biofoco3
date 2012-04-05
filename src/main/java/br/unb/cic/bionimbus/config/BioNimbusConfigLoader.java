package br.unb.cic.bionimbus.config;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public final class BioNimbusConfigLoader {

	private BioNimbusConfigLoader() {}
	
	public static BioNimbusConfig loadHostConfig(String filename) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
//		HostConfig hostConfig = mapper.readValue(new File(filename), HostConfig.class);
		return mapper.readValue(new File(filename), BioNimbusConfig.class);
//		BioNimbusConfig config = new BioNimbusConfig();
//		config.setHost(new Host(hostConfig.getAddress(), hostConfig.getPort()));
//		return config;
	}

}

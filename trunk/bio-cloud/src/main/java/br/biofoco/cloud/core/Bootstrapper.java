package br.biofoco.cloud.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.biofoco.cloud.config.HostConfig;

public class Bootstrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrapper.class);
	
	public static void main(String[] args) throws Exception {
		
		LOGGER.debug("Starting peer node...");
		
		HostConfig config = new HostConfig();		
		JettyRunner jetty = new JettyRunner();		
		jetty.start(config.getHttpPort());
	}
}

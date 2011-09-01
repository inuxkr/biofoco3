package br.unb.cic.bionimbus.config;

import br.unb.cic.bionimbus.p2p.Host;

public class BioNimbusConfig {

	private Host host;

	public boolean isClient() {
		return false;
	}
	
	public String getInfra() {
		return "hadoop";
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
}

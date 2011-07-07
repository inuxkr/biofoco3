package br.unb.cic.bionimbus.config;

public class BioNimbusConfig {

	public boolean isClient() {
		return false;
	}
	
	public String getInfra() {
		return "hadoop";
	}
}

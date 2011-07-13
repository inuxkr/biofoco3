package br.unb.cic.bionimbus.plugin;

import br.unb.cic.bionimbus.p2p.BioNimbusP2P;

public interface Plugin {

	public void start();
	
	public void shutdown();

	public void setP2P(BioNimbusP2P p2p);
}

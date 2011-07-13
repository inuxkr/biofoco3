package br.unb.cic.bionimbus;

import br.unb.cic.bionimbus.p2p.BioNimbusP2P;

public interface Service {
	
	public void start(BioNimbusP2P p2p);
	
	public void shutdown();
	
	public void getStatus();

}

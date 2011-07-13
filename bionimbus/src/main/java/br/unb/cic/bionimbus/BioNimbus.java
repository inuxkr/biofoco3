package br.unb.cic.bionimbus;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.p2p.BioNimbusP2P;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginFactory;

public class BioNimbus implements P2PListener {

	private Plugin plugin = null;
	private BioNimbusConfig config = null;
	private BioNimbusP2P p2p = null;

	public BioNimbus() {
		config = new BioNimbusConfig();
		plugin = null;

		if (!config.isClient()) {
			plugin = PluginFactory.getPlugin(config.getInfra());
			plugin.start();
		}

		p2p = new BioNimbusP2P(config);
		p2p.start();

		if (!config.isClient())
			plugin.setP2P(p2p);

		if (p2p.isMaster()) {
			ServiceManager manager = new ServiceManager();
			manager.startAll(/* p2p */);
		}

		p2p.addListener(this);
	}

	@Override
	public void onEvent(P2PEvent event) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		new BioNimbus();
	}
}

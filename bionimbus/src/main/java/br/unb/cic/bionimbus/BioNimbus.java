package br.unb.cic.bionimbus;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginFactory;

public class BioNimbus {

	public static void main(String[] args) {

		BioNimbusConfig config = new BioNimbusConfig();
		Plugin plugin = null;

		if (!config.isClient()) {
			plugin = PluginFactory.getPlugin(config.getInfra());
			plugin.start();
		}

		BioNimbusP2P p2p = new BioNimbusP2P();
		p2p.start(config.isClient());

		if (!config.isClient())
			plugin.setP2P(/*p2p*/);

		if (p2p.isMaster()) {
			ServiceManager manager = new ServiceManager();
			manager.startAll(/* p2p */);
		}
	}
}

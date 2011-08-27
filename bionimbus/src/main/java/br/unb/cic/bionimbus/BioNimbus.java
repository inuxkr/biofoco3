package br.unb.cic.bionimbus;

import java.io.IOException;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.config.BioNimbusConfigLoader;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.Plugin;
import br.unb.cic.bionimbus.plugin.PluginFactory;

public class BioNimbus implements P2PListener {

	private Plugin plugin = null;
	private P2PService p2p = null;
	private final BioNimbusConfig config;

	public BioNimbus(BioNimbusConfig config) {
		
		this.config = config;
		
		plugin = null;

		if (!config.isClient()) {
			plugin = PluginFactory.getPlugin(config.getInfra());
			plugin.start();
		}

		p2p = new P2PService();
		p2p.setConfig(config);
		p2p.start();

		if (!config.isClient())
			plugin.setP2P(p2p);

		if (p2p.isMaster()) {
			ServiceManager manager = new ServiceManager();
			manager.startAll(p2p);
		}

		//p2p.addListener(this);
	}

	@Override
	public void onEvent(P2PEvent event) {
	}

	public static void main(String[] args) throws IOException {
		
		String configFile = System.getProperty("config.file", "conf/peer.json");		
		BioNimbusConfig config = BioNimbusConfigLoader.loadHostConfig(configFile);
				
		new BioNimbus(config);
	}
}

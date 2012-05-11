package br.unb.cic.bionimbus.plugin;

import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.hadoop.HadoopPlugin;
import br.unb.cic.bionimbus.plugin.linux.LinuxPlugin;

public class PluginFactory {

	private static Plugin REF;

	public static synchronized Plugin getPlugin(String pluginType, P2PService p2p) {
		if (REF == null) {
			if (pluginType.equals("hadoop"))
				REF = new HadoopPlugin();
			else if (pluginType.equals("linux"))
				REF = new LinuxPlugin(p2p);
		}
		return REF;
	}

}

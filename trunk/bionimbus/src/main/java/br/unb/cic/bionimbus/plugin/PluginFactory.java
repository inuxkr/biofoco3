package br.unb.cic.bionimbus.plugin;

import br.unb.cic.bionimbus.plugin.hadoop.HadoopPlugin;

public class PluginFactory {
	
	public static Plugin getPlugin(String pluginType) {
		if (pluginType == "hadoop")
			return new HadoopPlugin();
		else
			return null;
	}

}

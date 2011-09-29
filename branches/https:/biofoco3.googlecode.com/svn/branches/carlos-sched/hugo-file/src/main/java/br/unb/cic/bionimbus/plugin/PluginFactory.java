package br.unb.cic.bionimbus.plugin;

import br.unb.cic.bionimbus.plugin.hadoop.HadoopPlugin;

public class PluginFactory {

	private static Plugin REF;

	public static synchronized Plugin getPlugin(String pluginType) {
		if (REF == null && pluginType.equals("hadoop"))
			REF = new HadoopPlugin();
		return REF;
	}

}

package br.unb.cic.bionimbus.storage.policy.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.storage.policy.StoragePolicy;
import br.unb.cic.bionimbus.utils.Pair;

public class BasicStoragePolicy extends StoragePolicy {
	
	public static Double PESO_LATENCY = 0.50;
	public static Double PESO_DISTANCIA = 0.30;
	public static Double PESO_CUSTO = 0.10;
	public static Double PESO_CARGA_TRABALHO = 0.10;
	
	public static Integer NUMBER_OF_COPIES = 2;

	public Collection<PluginInfo> getBestPluginsForStore(File file) {

		Map<Double, PluginInfo> ranking = new TreeMap<Double, PluginInfo>();
		Double maxLatency = Double.MAX_VALUE;
		Double maxDistancia = Double.MAX_VALUE;
		Double maxCusto = Double.MAX_VALUE;
		Double maxCargaTrabalho = Double.MAX_VALUE;
		
		//Busca os melhores valores para os aspectos
		for (PluginInfo pluginInfo : getCloudMap().values()) {
			if (pluginInfo.getLatency() > maxLatency)
				maxLatency = new Long(pluginInfo.getLatency()).doubleValue();
			if (pluginInfo.getDistance() > maxDistancia)
				maxDistancia = pluginInfo.getDistance().doubleValue();
			if (pluginInfo.getCostKb() > maxCusto)
				maxCusto = pluginInfo.getCostKb().doubleValue();
			if (pluginInfo.getWorkLoad() > maxCargaTrabalho)
				maxCargaTrabalho = pluginInfo.getWorkLoad().doubleValue();
		}

		Collection<PluginInfo> best = new ArrayList<PluginInfo>();
		for (PluginInfo pluginInfo : getCloudMap().values()) {
			// Somente considera a cloud se a mesma possuir espaço suficiente para armazernar o arquivo
			Double value = (1-(pluginInfo.getLatency()/maxLatency)*PESO_LATENCY) + 
						   (1-(pluginInfo.getDistance()/maxDistancia)*PESO_DISTANCIA) +
						   (1-(pluginInfo.getCostKb()/maxCusto)*PESO_CUSTO) +
						   (1-(pluginInfo.getWorkLoad()/maxCargaTrabalho)*PESO_CARGA_TRABALHO);
			ranking.put(value, pluginInfo);
		}
		
		Object[] chaves = ranking.keySet().toArray();
        for (int i = 0; i < NUMBER_OF_COPIES ; i++) {
        	best.add(ranking.get(chaves[chaves.length-i]));
		}

		return best;
	}
	
	@Override
	public Collection<PluginInfo> getBestPluginsForRestore(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean store(File file) {
		Collection<PluginInfo> availablePlugins = getBestPluginsForStore(file);
    	if (availablePlugins.size() == 0) {
    		return false;
    	}
		return false;
	}

	@Override
	public File restore() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

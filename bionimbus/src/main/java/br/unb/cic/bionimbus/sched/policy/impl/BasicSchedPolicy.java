package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;

public class BasicSchedPolicy extends SchedPolicy {
	
	private final int CORES_WEIGHT = 3;
	private final int NODES_WEIGHT = 2;

	private List<PluginInfo> filterByService(long serviceId) throws SchedException {
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		for (PluginInfo pluginInfo : getCloudMap().values()) {
			if (pluginInfo.getService(serviceId) != null)
				plugins.add(pluginInfo);
		}
		
		if (plugins.size() == 0) {
			throw new SchedException("Service not available.");
		}
		
		return plugins;
	}
	
	private PluginInfo getBestPluginForJob(List<PluginInfo> plugins, JobInfo job) throws SchedException {
		// O que fazer para pegar o tamanho do job?
		// Como fazer para pegar a lista de jobs em execucao no momento? E a de jobs pendentes?
		
		if (plugins.size() == 0) {
			throw new SchedException("Empty plugins list sent to sched alghorithm.");
		}
		
		PluginInfo best = plugins.get(0);
		for (PluginInfo plugin : plugins) {
			if (calculateWeightSum(plugin) > calculateWeightSum(best)) best = plugin;
		}
		
		return best;
	}
	
	private int calculateWeightSum(PluginInfo plugin) {
		return (plugin.getNumCores() * CORES_WEIGHT) + (plugin.getNumNodes() * NODES_WEIGHT);
	}

	@Override
    public HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos) throws SchedException {
    	HashMap<JobInfo, PluginInfo> schedMap = new HashMap<JobInfo, PluginInfo>();
    	
    	for (JobInfo jobInfo : jobInfos) {
    		PluginInfo resource = this.scheduleJob(jobInfo);
    		schedMap.put(jobInfo, resource);
    	}
    	
   		return schedMap;
    }

    public PluginInfo scheduleJob(JobInfo jobInfo) throws SchedException {
    	List<PluginInfo> availablePlugins = filterByService(jobInfo.getServiceId());
		return getBestPluginForJob(availablePlugins, jobInfo);
    }
}

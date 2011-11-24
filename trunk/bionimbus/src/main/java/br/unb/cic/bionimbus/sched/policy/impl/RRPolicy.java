package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;

public class RRPolicy extends SchedPolicy {

        private List<JobInfo> jobs;
        
        private List<PluginInfo> usedResources;
        
        public void addJob(JobInfo jobInfo) throws SchedException {
                jobs.add(jobInfo);
        }
        
        public PluginInfo scheduleJob(JobInfo jobInfo) {
        	List<PluginInfo> plugins = filterByService(jobInfo.getServiceId(), filterByUsed());
        	if (plugins.size() == 0) {
    			return null;
    		}
            return plugins.get(0);
        }
        
        @Override
        public HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos) {
        	HashMap<JobInfo, PluginInfo> schedMap = new HashMap<JobInfo, PluginInfo>();
        	
        	for (JobInfo jobInfo : jobInfos) {
        		PluginInfo resource = this.scheduleJob(jobInfo);
        		schedMap.put(jobInfo, resource);
        		usedResources.add(resource);
        	}
        	
       		return schedMap;
        }
        
        private List<PluginInfo> filterByService(long serviceId, List<PluginInfo> plgs) {
                ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
                for (PluginInfo pluginInfo : plgs) {
                        if (pluginInfo.getService(serviceId) != null)
                                plugins.add(pluginInfo);
                }
                
                return plugins;
        }
        
        private List<PluginInfo> filterByUsed() {
        	ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
        	for (PluginInfo pluginInfo : getCloudMap().values()) {
                if (!usedResources.contains(pluginInfo))
                        plugins.add(pluginInfo);
        	}
        
        	if (plugins.size() == 0) {
                return new ArrayList<PluginInfo>(getCloudMap().values());
        	}
        
        	return plugins;
        }
}

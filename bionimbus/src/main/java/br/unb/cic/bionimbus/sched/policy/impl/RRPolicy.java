package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.ArrayList;
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
        
        public PluginInfo scheduleJob(JobInfo jobInfo) throws SchedException {
        	List<PluginInfo> plugins = filterByService(jobInfo.getServiceId(), filterByUsed());
            return plugins.get(0);
        }
        
        @Override
        public PluginInfo schedule(JobInfo... jobInfos) throws SchedException {
        	PluginInfo resource = this.scheduleJob(jobInfos[0]);
        	usedResources.add(resource);
       		return resource;
        }
        
        private List<PluginInfo> filterByService(long serviceId, List<PluginInfo> plgs) throws SchedException {
                ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
                for (PluginInfo pluginInfo : plgs) {
                        if (pluginInfo.getService(serviceId) != null)
                                plugins.add(pluginInfo);
                }
                
                if (plugins.size() == 0) {
                        throw new SchedException("Service not available.");
                }
                
                return plugins;
        }
        
        private List<PluginInfo> filterByUsed() throws SchedException {
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
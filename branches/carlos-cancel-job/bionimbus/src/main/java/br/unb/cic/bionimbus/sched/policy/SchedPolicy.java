package br.unb.cic.bionimbus.sched.policy;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.policy.impl.AHPPolicy;


public abstract class SchedPolicy {
	private ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

	public void setCloudMap(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		this.cloudMap = cloudMap;
	}
	
	protected ConcurrentHashMap<String, PluginInfo> getCloudMap() {
		return this.cloudMap;
	}
	
	public static SchedPolicy getInstance(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		SchedPolicy policy = new AHPPolicy();
		policy.setCloudMap(cloudMap);
		return policy;
	}
	
	public abstract HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos);
}

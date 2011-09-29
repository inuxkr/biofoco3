package br.unb.cic.bionimbus.sched.policy;

import java.util.concurrent.ConcurrentHashMap;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.impl.BasicSchedPolicy;

public abstract class SchedPolicy {
	private ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

	protected void setCloudMap(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		this.cloudMap = cloudMap;
	}
	
	protected ConcurrentHashMap<String, PluginInfo> getCloudMap() {
		return this.cloudMap;
	}
	
	public static SchedPolicy getInstance(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		SchedPolicy policy = new BasicSchedPolicy();
		policy.setCloudMap(cloudMap);
		return policy;
	}
	
	public abstract PluginInfo schedule(JobInfo jobInfo) throws SchedException;
}

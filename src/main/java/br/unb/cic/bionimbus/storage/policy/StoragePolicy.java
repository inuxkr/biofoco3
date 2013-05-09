package br.unb.cic.bionimbus.storage.policy;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.storage.policy.impl.BasicStoragePolicy;


public abstract class StoragePolicy {
	
	private ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

	public void setCloudMap(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		this.cloudMap = cloudMap;
	}
	
	protected ConcurrentHashMap<String, PluginInfo> getCloudMap() {
		return this.cloudMap;
	}
	
	public static StoragePolicy getInstance() {
		StoragePolicy policy = new BasicStoragePolicy();
		return policy;
	}
	
	public static StoragePolicy getInstance(ConcurrentHashMap<String, PluginInfo> cloudMap) {
		StoragePolicy policy = new BasicStoragePolicy();
		policy.setCloudMap(cloudMap);
		return policy;
	}
	
	public abstract boolean store(File file);
	
	public abstract File restore();
	
	public abstract Collection<PluginInfo> getBestPluginsForStore(File file);
	
	public abstract Collection<PluginInfo> getBestPluginsForRestore(File file);

}

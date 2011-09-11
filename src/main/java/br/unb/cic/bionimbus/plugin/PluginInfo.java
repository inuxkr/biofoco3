package br.unb.cic.bionimbus.plugin;

import java.util.List;

import br.unb.cic.bionimbus.p2p.Host;

public class PluginInfo {
	
	private String id;
	
	private Host host;
	
	private Integer numCores;
	
	private Integer numNodes;
	
	private Float fsSize;
	
	private Float fsFreeSize;
	
	private List<PluginService> services;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
	public Float getFsFreeSize() {
		return fsFreeSize;
	}

	public void setFsFreeSize(Float fsFreeSize) {
		this.fsFreeSize = fsFreeSize;
	}

	public Integer getNumCores() {
		return numCores;
	}

	public void setNumCores(Integer numCores) {
		this.numCores = numCores;
	}

	public Integer getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(Integer numNodes) {
		this.numNodes = numNodes;
	}

	public Float getFsSize() {
		return fsSize;
	}

	public void setFsSize(Float fsSize) {
		this.fsSize = fsSize;
	}

	public List<PluginService> getServices() {
		return services;
	}

	public void setServices(List<PluginService> services) {
		this.services = services;
	}

	public PluginService getService(long serviceId) {
		for (PluginService service : getServices())
			if (service.getId() == serviceId)
				return service;
		return null;
	}
}

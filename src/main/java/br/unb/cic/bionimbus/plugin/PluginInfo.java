package br.unb.cic.bionimbus.plugin;

import java.util.List;

import br.unb.cic.bionimbus.p2p.Host;

public class PluginInfo implements PluginOps {
	
	private String id;
	
	private Host host;
	
	private long uptime;
	
	private long latency;
	
	private long timestamp;
	
	private Integer numCores;
	
	private Integer numNodes;
	
	private Integer numOccupied;
	
	private Float fsSize;
	
	private Float fsFreeSize;
	
	private Float memorySize;
	
	private Float memoryFreeSize;
	
	private Float costKb;
	
	private Integer workLoad;
	
	private Integer distance;
	
	private String ip;
	
	private Double latitude;
	
	private Double longitude;
	
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

	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public long getLatency() {
		return latency;
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
	
	public Integer getNumOccupied() {
		return numOccupied;
	}

	public void setNumOccupied(Integer numOccupied) {
		this.numOccupied = numOccupied;
	}
	
	public Float getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(Float memorySize) {
		this.memorySize = memorySize;
	}

	public Float getMemoryFreeSize() {
		return memoryFreeSize;
	}

	public void setMemoryFreeSize(Float memoryFreeSize) {
		this.memoryFreeSize = memoryFreeSize;
	}

	public Float getCostKb() {
		return costKb;
	}

	public void setCostKb(Float costKb) {
		this.costKb = costKb;
	}

	public Integer getWorkLoad() {
		return workLoad;
	}

	public void setWorkLoad(Integer workLoad) {
		this.workLoad = workLoad;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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
	
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof PluginInfo)) {
			return false;
		}
		
		PluginInfo other = (PluginInfo) object;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}

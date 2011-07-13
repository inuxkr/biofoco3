package br.unb.cic.bionimbus.plugin;

public class PluginInfo {
	
	private Integer numCores;
	
	private Integer numNodes;
	
	private Float fsSize;
	
	private Float fsFreeSize;

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

}
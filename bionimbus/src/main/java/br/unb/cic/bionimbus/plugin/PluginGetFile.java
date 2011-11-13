package br.unb.cic.bionimbus.plugin;

import br.unb.cic.bionimbus.p2p.PeerNode;

public class PluginGetFile {
	
	private PluginFile pluginFile;
	
	private String taskId;
	
	private PeerNode peer;

	public PluginFile getPluginFile() {
		return pluginFile;
	}

	public void setPluginFile(PluginFile pluginFile) {
		this.pluginFile = pluginFile;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public PeerNode getPeer() {
		return peer;
	}

	public void setPeer(PeerNode peer) {
		this.peer = peer;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof PluginGetFile)) {
			return false;
		}
		
		PluginGetFile other = (PluginGetFile) object;
		
		return (this.pluginFile.equals(other.pluginFile) && this.taskId.equals(other.taskId));
	}
	
	@Override
	public int hashCode() {
		return (pluginFile.toString() + taskId).hashCode();
	}
	
	@Override
	public String toString() {
		return pluginFile.toString() + taskId;
	}
}

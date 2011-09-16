package br.unb.cic.bionimbus.plugin;

import java.util.UUID;

public class PluginFile {
	
	private String id = UUID.randomUUID().toString();
	
	private String path;
	
	private String pluginId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof PluginTask)) {
			return false;
		}
		
		PluginFile other = (PluginFile) object;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id.toString() + ":" + path;
	}
}

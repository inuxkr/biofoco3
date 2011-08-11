package br.unb.cic.bionimbus.plugin;

import java.util.UUID;

public class PluginTask {
	
	private PluginTaskState state = PluginTaskState.WAITING;
	
	private String id = UUID.randomUUID().toString();

	public PluginTaskState getState() {
		return state;
	}

	public void setState(PluginTaskState state) {
		this.state = state;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof PluginTask)) {
			return false;
		}
		
		PluginTask other = (PluginTask) object;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id.toString() + ":" + state;
	}

}

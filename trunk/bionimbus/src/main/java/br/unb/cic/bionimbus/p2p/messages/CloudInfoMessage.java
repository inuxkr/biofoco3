package br.unb.cic.bionimbus.p2p.messages;

import java.util.Collection;

import br.unb.cic.bionimbus.plugin.PluginInfo;

public class CloudInfoMessage implements Message {
	
	private Collection<PluginInfo> values;
	
	public CloudInfoMessage(Collection<PluginInfo> values) {
		this.values = values;
	}
	
	public Collection<PluginInfo> values() {
		return values;
	}

	@Override
	public MessageType getType() {
		return MessageType.CLOUDINFO;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

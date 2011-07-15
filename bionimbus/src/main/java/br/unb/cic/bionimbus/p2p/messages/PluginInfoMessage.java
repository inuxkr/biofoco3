package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.plugin.PluginInfo;

public class PluginInfoMessage implements Message {
	
	private PluginInfo info;
	
	public PluginInfoMessage(PluginInfo info) {
		this.info = info;
	}

	@Override
	public MessageType getType() {
		return MessageType.PLUGININFO;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

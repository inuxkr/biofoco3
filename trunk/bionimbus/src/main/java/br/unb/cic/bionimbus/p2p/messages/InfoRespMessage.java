package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class InfoRespMessage implements Message {
	
	private PluginInfo pluginInfo;
	
	public InfoRespMessage(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.INFORESP.ordinal();
	}

}

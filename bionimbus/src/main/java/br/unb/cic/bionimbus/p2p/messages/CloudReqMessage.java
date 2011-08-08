package br.unb.cic.bionimbus.p2p.messages;

import java.util.Collection;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class CloudReqMessage implements Message {
	
	private Collection<PluginInfo> values;
	
	public CloudReqMessage(Collection<PluginInfo> values) {
		this.values = values;
	}
	
	public Collection<PluginInfo> values() {
		return values;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.CLOUDREQ.ordinal();
	}

}

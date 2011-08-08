package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class StatusRespMessage implements Message {
	
	private PluginTask task;
	
	public StatusRespMessage(PluginTask task) {
		this.task = task;
	}
	
	public PluginTask getPluginTask() {
		return task;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSRESP.ordinal();
	}

}

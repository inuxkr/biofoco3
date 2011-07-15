package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.plugin.PluginTask;

public class FinishedTaskMessage implements Message {
	
	private PluginTask task;
	
	public FinishedTaskMessage(PluginTask task) {
		this.task = task;
	}

	@Override
	public MessageType getType() {
		return MessageType.FINISHEDTASK;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

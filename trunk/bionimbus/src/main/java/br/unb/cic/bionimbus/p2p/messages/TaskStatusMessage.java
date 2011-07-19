package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.plugin.PluginTask;

public class TaskStatusMessage implements Message {
	
	private PluginTask task;
	
	public TaskStatusMessage(PluginTask task) {
		this.task = task;
	}
	
	public PluginTask getTask() {
		return task;
	}

	@Override
	public MessageType getType() {
		return MessageType.TASKSTATUS;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

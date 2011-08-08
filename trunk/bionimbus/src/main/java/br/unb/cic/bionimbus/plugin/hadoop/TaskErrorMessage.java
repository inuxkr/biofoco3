package br.unb.cic.bionimbus.plugin.hadoop;

import br.unb.cic.bionimbus.p2p.P2PErrorType;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;

public class TaskErrorMessage extends ErrorMessage {
	
	private String pluginId;
	
	private String taskId;
	
	public TaskErrorMessage(String pluginId, String taskId, String error) {
		super(error);
		this.pluginId = pluginId;
		this.taskId = taskId;
	}

	public String getPluginId() {
		return pluginId;
	}
	
	public String getTaskId() {
		return taskId;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PErrorType getErrorType() {
		return P2PErrorType.TASK;
	}
}

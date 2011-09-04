package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PErrorType;
import br.unb.cic.bionimbus.p2p.PeerNode;

public class TaskErrorMessage extends ErrorMessage {
	
	private String pluginId;
	
	private String taskId;
	
	public TaskErrorMessage(PeerNode peer, String pluginId, String taskId, String error) {
		super(peer, error);
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
	public void deserialize(byte[] buffer) throws Exception {
		// TODO Auto-generated method stub
		
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

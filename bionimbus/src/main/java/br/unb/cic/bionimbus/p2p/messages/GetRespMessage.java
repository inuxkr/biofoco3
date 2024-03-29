package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class GetRespMessage extends AbstractMessage {
	
	private PluginInfo pluginInfo;
	
	private PluginFile pluginFile;
	
	private String taskId;
	
	public GetRespMessage() {
		super();
	}
	
	public GetRespMessage(PeerNode peer, PluginInfo pluginInfo, PluginFile pluginFile, String taskId) {
		super(peer);
		this.pluginInfo = pluginInfo;
		this.pluginFile = pluginFile;
		this.taskId = taskId;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	public PluginFile getPluginFile() {
		return pluginFile;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	@Override
	public byte[] serialize() throws Exception {		
		BulkMessage message = encodeBasicMessage();
		message.setPluginInfo(pluginInfo);
		message.setPluginFile(pluginFile);
		if (taskId.length() > 0)
			message.setTaskId(taskId);
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);		
		this.pluginInfo = message.getPluginInfo();
		this.pluginFile = message.getPluginFile();
		this.taskId = message.getTaskId();
	}

	@Override
	public int getType() {
		return P2PMessageType.GETRESP.code();
	}

}

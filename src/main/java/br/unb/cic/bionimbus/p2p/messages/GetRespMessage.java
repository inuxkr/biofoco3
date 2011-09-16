package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class GetRespMessage extends AbstractMessage {
	
	private PluginInfo pluginInfo;
	
	private PluginFile pluginFile;
	
	public GetRespMessage() {
		super();
	}
	
	public GetRespMessage(PeerNode peer, PluginInfo pluginInfo, PluginFile pluginFile) {
		super(peer);
		this.pluginInfo = pluginInfo;
		this.pluginFile = pluginFile;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	public PluginFile getPluginFile() {
		return pluginFile;
	}
	
	@Override
	public byte[] serialize() throws Exception {		
		BulkMessage message = encodeBasicMessage();
		message.setPluginInfo(pluginInfo);
		message.setPluginFile(pluginFile);
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);		
		this.pluginInfo = message.getPluginInfo();
		this.pluginFile = message.getPluginFile();
	}

	@Override
	public int getType() {
		return P2PMessageType.GETRESP.code();
	}

}

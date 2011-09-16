package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class PrepRespMessage extends AbstractMessage {

	private PluginInfo pluginInfo;

	private PluginFile pluginFile;

	public PrepRespMessage() {
		super();
	}

	public PrepRespMessage(PeerNode peer, PluginInfo pluginInfo, PluginFile pluginFile) {
		super(peer);
		this.pluginInfo = pluginInfo;
		this.pluginFile = pluginFile;
	}

	public PluginFile getPluginFile() {
		return pluginFile;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	@Override
	public byte[] serialize() throws Exception {
		BulkMessage message = encodeBasicMessage();
		message.setPluginFile(pluginFile);
		message.setPluginInfo(pluginInfo);
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);
		this.pluginFile = message.getPluginFile();
		this.pluginInfo = message.getPluginInfo();
	}

	@Override
	public int getType() {
		return P2PMessageType.PREPRESP.code();
	}

}

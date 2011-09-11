package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.client.FileInfo;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class StoreRespMessage extends AbstractMessage {
	
	private PluginInfo pluginInfo;
	
	private FileInfo fileInfo;
	
	public StoreRespMessage() {
		super();
	}
	
	public StoreRespMessage(PeerNode peer, PluginInfo pluginInfo, FileInfo fileInfo) {
		super(peer);
		this.pluginInfo = pluginInfo;
		this.fileInfo = fileInfo;
	}
	
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	@Override
	public int getType() {
		return P2PMessageType.STORERESP.code();
	}
	
	@Override
	public byte[] serialize() throws Exception {
		BulkMessage message = encodeBasicMessage();
		message.setPluginInfo(pluginInfo);
		message.setFileInfo(fileInfo);
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);
		this.pluginInfo = message.getPluginInfo();
		this.fileInfo = message.getFileInfo();
	}

}

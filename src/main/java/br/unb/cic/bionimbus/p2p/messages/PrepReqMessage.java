package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class PrepReqMessage extends AbstractMessage {
	
	private PluginFile pluginFile;
	
	public PrepReqMessage() {
		super();
	}
	
	public PrepReqMessage(PeerNode peer, PluginFile pluginFile) {
		super(peer);
		this.pluginFile = pluginFile;
	}
	
	public PluginFile getPluginFile() {
		return pluginFile;
	}

	@Override
	public byte[] serialize() throws Exception {
		BulkMessage message = encodeBasicMessage();
		message.setPluginFile(pluginFile);				
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);		
		this.pluginFile = message.getPluginFile();
	}

	@Override
	public int getType() {
		return P2PMessageType.PREPREQ.code();
	}

}

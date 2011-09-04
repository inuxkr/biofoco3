package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginInfo;

import com.google.common.base.Charsets;

public class InfoRespMessage extends AbstractMessage implements Message {

	private PluginInfo pluginInfo;
	
	public InfoRespMessage() {
		super();
	}

	public InfoRespMessage(PeerNode peer, PluginInfo pluginInfo) {
		super(peer);
		this.pluginInfo = pluginInfo;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		BulkMessage message = mapper.readValue(buffer, BulkMessage.class);
		
		String id = message.getPeerID();
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(message.getHost());
		
		pluginInfo = message.getPluginInfo();
	}

	@Override
	public byte[] serialize() throws Exception {
		
		BulkMessage message = new BulkMessage();
		message.setPeerID(peer.getId().toString());
		message.setHost(peer.getHost());
		message.setPluginInfo(pluginInfo);
		
		ObjectMapper mapper = new ObjectMapper();
		String raw = mapper.writeValueAsString(message);
		return raw.getBytes(Charsets.UTF_8);		
		
	}

	@Override
	public int getType() {
		return P2PMessageType.INFORESP.code();
	}

}

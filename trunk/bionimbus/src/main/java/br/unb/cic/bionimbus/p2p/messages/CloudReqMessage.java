package br.unb.cic.bionimbus.p2p.messages;

import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class CloudReqMessage implements Message {
	
	private Collection<PluginInfo> values;
	
	public CloudReqMessage() {
	}
	
	public CloudReqMessage(Collection<PluginInfo> values) {
		this.values = values;
	}
	
	public Collection<PluginInfo> values() {
		return values;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		this.values = mapper.readValue(buffer, new TypeReference<Collection<PluginInfo>>(){});
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return null;
	}

	@Override
	public int getType() {
		return P2PMessageType.CLOUDREQ.ordinal();
	}

}

package br.unb.cic.bionimbus.p2p.messages;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class CloudRespMessage implements Message {

	private Collection<PluginInfo> values;

	public CloudRespMessage() {
	}

	public CloudRespMessage(Collection<PluginInfo> values) {
		this.values = values;
	}

	public Collection<PluginInfo> values() {
		return values;
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(values);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		values = mapper.readValue(buffer, mapper.getTypeFactory()
				.constructCollectionType(ArrayList.class, PluginInfo.class));
	}

	@Override
	public int getType() {
		return P2PMessageType.CLOUDRESP.ordinal();
	}

}

package br.unb.cic.bionimbus.p2p.messages;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginInfo;

public class InfoRespMessage implements Message {

	private PluginInfo pluginInfo;
	
	public InfoRespMessage() {
	}

	public InfoRespMessage(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		this.pluginInfo = mapper.readValue(buffer, PluginInfo.class);
	}

	@Override
	public byte[] serialize() throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(getPluginInfo());
	}

	@Override
	public int getType() {
		return P2PMessageType.INFORESP.ordinal();
	}

}

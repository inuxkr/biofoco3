package br.unb.cic.bionimbus.p2p.messages;

import java.util.Collection;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class CloudRespMessage extends AbstractMessage {

	private Collection<PluginInfo> values;

	public CloudRespMessage() {
		super();
	}

	public CloudRespMessage(PeerNode peer, Collection<PluginInfo> values) {
		super(peer);
		this.values = values;
	}

	public Collection<PluginInfo> values() {
		return values;
	}

	@Override
	public byte[] serialize() throws Exception {		
		BulkMessage message = encodeBasicMessage();
		message.setValues(values);				
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		values = mapper.readValue(buffer, mapper.getTypeFactory().constructCollectionType(ArrayList.class, PluginInfo.class));
		
		BulkMessage message = decodeBasicMessage(buffer);		
		values = message.getValues();
	}

	@Override
	public int getType() {
		return P2PMessageType.CLOUDRESP.code();
	}

}

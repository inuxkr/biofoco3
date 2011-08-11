package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class StatusRespMessage implements Message {
	
	private PluginTask task;
	
	public StatusRespMessage(){
	}
	
	public StatusRespMessage(PluginTask task) {
		this.task = task;
	}
	
	public PluginTask getPluginTask() {
		return task;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		this.task = mapper.readValue(buffer, PluginTask.class);
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(task);
	}

	@Override
	public int getType() {
		return P2PMessageType.STATUSRESP.ordinal();
	}

}

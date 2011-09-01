package br.unb.cic.bionimbus.p2p.messages;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class SchedRespMessage implements Message {
	
	private String jobId;
	private String pluginId;
	
	public SchedRespMessage() {
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("jobId", jobId);
		data.put("pluginId", pluginId);
		return mapper.writeValueAsBytes(data);
		
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = mapper.readValue(buffer, Map.class);
		this.jobId = (String) data.get("jobId");
		this.pluginId = (String) data.get("pluginId");
	}

	@Override
	public int getType() {
		return P2PMessageType.SCHEDRESP.ordinal();
	}

}

package br.unb.cic.bionimbus.p2p.messages;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class StartRespMessage implements Message {
	
	private String jobId;
	
	private PluginTask task;
	
	public StartRespMessage() {
	}
	
	public StartRespMessage(String jobId, PluginTask task) {
		this.jobId = jobId;
		this.task = task;
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public PluginTask getPluginTask() {
		return task;
	}

	@Override
	public byte[] serialize() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("jobId", getJobId());
		data.put("task", getPluginTask());

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(data);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = mapper.readValue(buffer, Map.class);
		this.jobId = (String) data.get("jobId");
		this.task = (PluginTask) data.get("task");
	}

	@Override
	public int getType() {
		return P2PMessageType.STARTRESP.ordinal();
	}

}
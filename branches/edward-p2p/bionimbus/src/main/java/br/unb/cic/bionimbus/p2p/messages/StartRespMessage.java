package br.unb.cic.bionimbus.p2p.messages;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;

public class StartRespMessage extends AbstractMessage {
	
	private String jobId;
	
	private PluginTask task;
	
	public StartRespMessage() {
		super();
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

		this.task = new PluginTask();
		Map<String, Object> taskMap = (Map<String, Object>) data.get("task");
		this.task.setId((String) taskMap.get("id"));
		this.task.setState(PluginTaskState.valueOf((String) taskMap.get("state")));
	}

	@Override
	public int getType() {
		return P2PMessageType.STARTRESP.code();
	}

}

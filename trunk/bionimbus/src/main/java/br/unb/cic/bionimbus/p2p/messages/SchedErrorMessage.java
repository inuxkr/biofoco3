package br.unb.cic.bionimbus.p2p.messages;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PErrorType;

public class SchedErrorMessage extends ErrorMessage implements Message {
	
	private String jobId;
	
	public SchedErrorMessage() {
		super(null);
		this.jobId = null;
	}
	
	public SchedErrorMessage(String jobId, String error) {
		super(error);
		this.jobId = jobId;
	}
	
	public String getJobId() {
		return jobId;
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type", getErrorType());
		data.put("error", getError());
		data.put("jobId", jobId);
		return mapper.writeValueAsBytes(data);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = mapper.readValue(buffer, Map.class);
		setError((String) data.get("error"));
		this.jobId = (String) data.get("jobId");
	}

	@Override
	public P2PErrorType getErrorType() {
		return P2PErrorType.SCHED;
	}

}

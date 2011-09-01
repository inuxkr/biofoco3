package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PMessageType;

public class SchedReqMessage implements Message {
	
	private JobInfo jobInfo;
	
	public SchedReqMessage() {
	}
	
	public SchedReqMessage(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}
	
	public JobInfo getJobInfo() {
		return jobInfo;
	}

	@Override
	public byte[] serialize() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(jobInfo);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		this.jobInfo = mapper.readValue(buffer, JobInfo.class);
	}

	@Override
	public int getType() {
		return P2PMessageType.SCHEDREQ.ordinal();
	}
}

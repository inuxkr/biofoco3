package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;

import com.google.common.base.Charsets;

public class StartReqMessage extends AbstractMessage {
	
	private JobInfo jobInfo;
	
	public StartReqMessage() {
		super();
	}
	
	public StartReqMessage(PeerNode peer, JobInfo jobInfo) {
		super(peer);
		this.jobInfo = jobInfo;
	}
	
	public JobInfo getJobInfo() {
		return jobInfo;
	}

	@Override
	public byte[] serialize() throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsBytes(jobInfo);
		
		BulkMessage message = new BulkMessage();
		message.setPeerID(peer.getId().toString());
		message.setHost(peer.getHost());
		message.setJobInfo(jobInfo);
		
		ObjectMapper mapper = new ObjectMapper();
		String raw = mapper.writeValueAsString(message);
		return raw.getBytes(Charsets.UTF_8);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		this.jobInfo = mapper.readValue(buffer, JobInfo.class);
		
		
		ObjectMapper mapper = new ObjectMapper();
		BulkMessage message = mapper.readValue(buffer, BulkMessage.class);
		
		String id = message.getPeerID();
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(message.getHost());
		
		jobInfo = message.getJobInfo();
	}

	@Override
	public int getType() {
		return P2PMessageType.STARTREQ.code();
	}

}

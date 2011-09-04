package br.unb.cic.bionimbus.p2p.messages;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;

import com.google.common.base.Charsets;

public class SchedRespMessage extends AbstractMessage {
	
	private String jobId;
	private String pluginId;
	
	public SchedRespMessage() {
		super();
	}
	
	public SchedRespMessage(PeerNode peer) {
		super(peer);
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
//		ObjectMapper mapper = new ObjectMapper();
//		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("jobId", jobId);
//		data.put("pluginId", pluginId);
//		return mapper.writeValueAsBytes(data);
		
		BulkMessage message = new BulkMessage();
		message.setPeerID(peer.getId().toString());
		message.setHost(peer.getHost());
		message.setJobId(jobId);
		message.setPluginId(pluginId);
		
		ObjectMapper mapper = new ObjectMapper();
		String raw = mapper.writeValueAsString(message);
		return raw.getBytes(Charsets.UTF_8);
		
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		Map<String, Object> data = mapper.readValue(buffer, Map.class);
//		this.jobId = (String) data.get("jobId");
//		this.pluginId = (String) data.get("pluginId");
		
		ObjectMapper mapper = new ObjectMapper();
		BulkMessage message = mapper.readValue(buffer, BulkMessage.class);
		
		String id = message.getPeerID();
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(message.getHost());
		
		jobId = message.getJobId();		
		pluginId = message.getPluginId();
		
	}

	@Override
	public int getType() {
		return P2PMessageType.SCHEDRESP.ordinal();
	}

}

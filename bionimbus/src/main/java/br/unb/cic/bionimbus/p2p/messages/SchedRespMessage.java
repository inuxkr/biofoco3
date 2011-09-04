package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.utils.JsonCodec;

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

		BulkMessage message = encodeBasicMessage();
		message.setJobId(jobId);
		message.setPluginId(pluginId);
		
		return JsonCodec.encodeMessage(message);
		
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		
		BulkMessage message = decodeBasicMessage(buffer);
		
		jobId = message.getJobId();		
		pluginId = message.getPluginId();		
	}

	@Override
	public int getType() {
		return P2PMessageType.SCHEDRESP.code();
	}

}

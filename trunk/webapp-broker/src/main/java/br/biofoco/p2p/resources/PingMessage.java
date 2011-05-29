package br.biofoco.p2p.resources;

public class PingMessage {
	
	private String id;
	private long timestamp;
	
	public PingMessage() {}

	public PingMessage(String id, long timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	

}

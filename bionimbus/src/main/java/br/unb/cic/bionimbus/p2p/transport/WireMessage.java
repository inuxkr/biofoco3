package br.unb.cic.bionimbus.p2p.transport;

public class WireMessage {

	private long id;
	private String body;
	
	// JSON needs this
	WireMessage() { }

	public WireMessage(long id, String body) {
		this.id = id;
		this.body = body;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public String toString() {
		return "id: " + id + " body: " + body;
	}
}

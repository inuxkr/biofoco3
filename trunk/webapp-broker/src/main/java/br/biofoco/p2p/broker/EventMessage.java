package br.biofoco.p2p.broker;

public class EventMessage {
	
	private String sender;
	private String receiver;
	private String body;
	
	public EventMessage() {
		
	}
	
	public EventMessage(String sender, String receiver, String body) {
		this.sender = sender;
		this.receiver = receiver;
		this.body = body;
	}
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}	
	
	@Override
	public int hashCode() {
		return sender.hashCode() + receiver.hashCode() + body.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		
		if (!(object instanceof EventMessage)) {
			return false;
		}
		
		EventMessage other = (EventMessage) object;
		
		return sender.equals(other.sender) && receiver.equals(other.receiver) && body.equals(other.body);
	}
}

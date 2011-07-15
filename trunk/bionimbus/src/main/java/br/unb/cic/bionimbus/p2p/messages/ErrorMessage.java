package br.unb.cic.bionimbus.p2p.messages;

public class ErrorMessage implements Message {
	
	private String description;
	
	public ErrorMessage(String description) {
		this.description = description;
	}

	@Override
	public MessageType getType() {
		return MessageType.ERROR;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return description;
	}

}

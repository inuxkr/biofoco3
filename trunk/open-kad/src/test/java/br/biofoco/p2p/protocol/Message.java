package br.biofoco.p2p.protocol;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

public final class Message {
	
	private long id;
	private MessageType type;
	private Long sender;
	private Long receiver;
	private String command;
	private byte[] data;
	
	@SuppressWarnings("unused")
	private Message() {	}
	
	public Message(long id, Long sender, Long receiver, MessageType type, String command, byte[] data) {
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		this.command = command;
		this.data = data;		
	}
	
	public long getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setReceiver(long receiver) {
		this.receiver = receiver;
	}

	public long getReceiver() {
		return receiver;
	}

	public void setSender(long sender) {
		this.sender = sender;
	}

	public long getSender() {
		return sender;
	}
	
	@Override
	public boolean equals(Object object){
		if (this == object)
			return true;
		
		if (!(object instanceof Message))
			return false;
		
		Message other = (Message) object;
		
		return Objects.equal(id, other.id) && Objects.equal(type, type);
	}
	
	@Override
	public int hashCode() {
		return Longs.hashCode(id);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
		              .add("id", id)
		              .add("sender", sender)
		              .add("receiver", receiver)
		              .add("type", type)
		              .add("command", command)
		              .add("data", data)
		              .toString();
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}	
}

package br.biofoco.p2p.protocol;

import java.util.Random;
import static java.lang.Math.*;

public final class MessageBuilder {
	
	private static final Random random = new Random(System.nanoTime());
	
	private long id;
	private MessageType messageType;
	private byte[] data;
	
	private Long sender;
	private Long receiver;

	private String command;
	
	private MessageBuilder() {}
	
	static MessageBuilder start() {
		return new MessageBuilder();
	}
	
	MessageBuilder id() {
		this.id = abs(random.nextLong());
		return this;
	}
	
	MessageBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	MessageBuilder data(byte[] data){
		this.data = data;
		return this;
	}
	
	MessageBuilder type(MessageType type) {
		this.messageType = type;
		return this;
	}
	
	MessageBuilder sender(Long sender) {
		this.sender = sender;
		return this;
	}
	
	MessageBuilder receiver(Long receiver) {
		this.receiver = receiver;
		return this;
	}
	
	MessageBuilder command(String command) {
		this.command = command;
		return this;
	}
	
	Message build() {
		return new Message(id, sender, receiver, messageType, command, data);
	}
}

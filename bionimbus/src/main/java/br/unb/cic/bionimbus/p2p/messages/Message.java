package br.unb.cic.bionimbus.p2p.messages;

public interface Message {
	
	MessageType getType();
	String toJSON();
}

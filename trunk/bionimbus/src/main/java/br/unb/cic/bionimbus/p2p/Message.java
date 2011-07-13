package br.unb.cic.bionimbus.p2p;

public interface Message {
	
	MessageType getType();
	String toJSON();
}

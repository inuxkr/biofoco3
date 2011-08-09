package br.unb.cic.bionimbus.messaging;

public interface Message {
	
	byte[] serialize();
	
	int getType();

}

package br.unb.cic.bionimbus.messaging;

public interface Message {
	
	byte[] serialize() throws Exception;
	
	int getType();

}

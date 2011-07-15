package br.unb.cic.bionimbus.p2p.messages;

public class GetInfoMessage implements Message {

	@Override
	public MessageType getType() {
		return MessageType.GETINFO;
	}

	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}

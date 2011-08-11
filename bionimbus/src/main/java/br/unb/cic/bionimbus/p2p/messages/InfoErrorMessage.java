package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.p2p.P2PErrorType;

public class InfoErrorMessage extends ErrorMessage {
	
	private String pluginId;
	
	public InfoErrorMessage(String pluginId, String error) {
		super(error);
		this.pluginId = pluginId;
	}
	
	public String getPluginId() {
		return pluginId;
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		this.pluginId = new String(buffer);
	}
	
	@Override
	public byte[] serialize() {
		return pluginId.getBytes();
	}

	@Override
	public P2PErrorType getErrorType() {
		return P2PErrorType.INFO;
	}

}

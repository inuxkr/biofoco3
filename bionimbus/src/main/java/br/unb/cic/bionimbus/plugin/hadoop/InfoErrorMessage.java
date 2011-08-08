package br.unb.cic.bionimbus.plugin.hadoop;

import br.unb.cic.bionimbus.p2p.P2PErrorType;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;

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
	public P2PErrorType getErrorType() {
		return P2PErrorType.INFO;
	}

	@Override
	public byte[] serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}

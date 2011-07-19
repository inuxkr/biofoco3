package br.unb.cic.bionimbus.p2p.transport;

import br.unb.cic.bionimbus.p2p.dht.Endpoint;

//FIXME: a única razão da existência desta classe é porque ainda não dá para serializar direto o ID
public class PeerCredentials {

	private String id;
	private Endpoint endpoint;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Endpoint getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	
}

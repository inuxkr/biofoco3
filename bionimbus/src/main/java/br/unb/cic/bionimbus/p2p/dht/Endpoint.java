package br.unb.cic.bionimbus.p2p.dht;

public class Endpoint {
	
	private int port;
	private String address;
	
	public Endpoint(){
		
	}
	
	public Endpoint(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public int getPort() {
		return port;
	}
		
	public String getAddress() {
		return address;
	}
		
	public void setPort(int port) {
		this.port = port;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return address + ":" + port;
	}
	
}

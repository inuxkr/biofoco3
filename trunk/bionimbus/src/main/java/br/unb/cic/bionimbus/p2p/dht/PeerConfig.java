package br.unb.cic.bionimbus.p2p.dht;

import java.util.HashSet;
import java.util.Set;

public class PeerConfig {
	
	private boolean dynamicID = true;
	private int port;
	private final Set<Endpoint> seeds = new HashSet<Endpoint>();
	private int processors = 1;
	private ID peerID;
	
	public boolean isDynamicID() {
		return dynamicID;
	}
	
	public void setDynamicID(boolean dynamicID) {
		this.dynamicID = dynamicID;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void addSeed(Endpoint host) {
		seeds .add(host);
	}
	
	public Set<Endpoint> getSeeds() {
		return seeds;
	}
	
	public void setProcessors(int processors) {
		this.processors = processors;
	}

	public int getProcessors() {
		return processors;
	}
	
	public void setPeerID(ID peerID) {
		this.peerID = peerID;
	}
	
	public ID getPeerID(){
		return peerID;
	}

}

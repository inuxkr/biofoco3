package br.unb.cic.bionimbus.p2p;


public class PeerNode implements Comparable<PeerNode> {

	private final ID id;
	private Host host;

	public PeerNode(ID id) {
		this.id = id;
	}

	public ID getId() {
		return id;
	}

	@Override
	public String toString() {
		return "[id:" + id + "]";
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object){
			return true;
		}
		if (!(object instanceof PeerNode)) {
			return false;
		}

		PeerNode other = (PeerNode) object;

		return this.id.equals(other.id);
	}

	@Override
	public int compareTo(PeerNode o) {
		return this.id.compareTo(o.id);
	}

	//TODO: RPC call
	public PeerNode retrieveSuccessor(ID key) {
		throw new UnsupportedOperationException();
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

}

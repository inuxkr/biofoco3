package br.biofoco.p2p.kad;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import br.biofoco.p2p.core.PeerNode;

import com.google.common.primitives.Longs;

public class KNode implements Comparable<KNode> {
	
	private final PeerNode peerNode;
	private long timestamp;
	
	public KNode(PeerNode peerNode, long timestamp) {
		
		checkNotNull(peerNode);		
		checkArgument(timestamp >= 0, "Node timestamp invalid");
		
		this.peerNode = peerNode;		
		this.timestamp = timestamp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof KNode))
			return false;
		
		KNode other = (KNode) obj;
		
		return this.peerNode.equals(other.peerNode);
	}
	
	@Override
	public int hashCode() {
		return peerNode.hashCode();
	}
	
	@Override
	public String toString() {
		return peerNode.toString() + ":" + timestamp;
	}

	public PeerNode getPeerNode() {
		return peerNode;
	}

	@Override
	public int compareTo(KNode o) {
		return Longs.compare(timestamp, o.timestamp);
	}

}

package br.biofoco.p2p.kad;

import java.util.Collection;
import java.util.Map;

import br.biofoco.p2p.core.ID;
import br.biofoco.p2p.core.PeerNode;

public class KadProtocol {
	
	public static final int ALPHA = 3; // parallelism level of RPC calls

	public void ping(PeerNode peer) throws KadException{
		throw new UnsupportedOperationException();
	}
	
	public void store(PeerNode node, ID key, byte[] value) throws KadException{
		throw new UnsupportedOperationException();
	}
	
	public Collection<PeerNode> find_node(ID id) throws KadException{
		throw new UnsupportedOperationException();
	}
	
	public Map<PeerNode, byte[]> find_value(ID id) throws KadException{
		throw new UnsupportedOperationException();
	}
	
}

package br.unb.cic.bionimbus.p2p.dht.walker;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import br.unb.cic.bionimbus.p2p.dht.ID;
import br.unb.cic.bionimbus.p2p.dht.PeerNode;

import com.google.common.collect.ImmutableSet;

public class PeerView  {

	private final ConcurrentSkipListMap<ID,PeerNode> peerView;
	
	public PeerView() {
		peerView = new ConcurrentSkipListMap<ID,PeerNode>();
	}
	
	public void add(PeerNode peer) {
		peerView.putIfAbsent(peer.getId(), peer);
//		System.out.println("peerview:" + peerView.keySet());
	}
	
	public boolean remove(PeerNode peer) {
		return peerView.remove(peer.getId(), peer);
	}
	
	public Set<ID> keys() {
		return ImmutableSet.copyOf(peerView.keySet());
	}

	public int size() {
		return peerView.size();
	}

	public Set<PeerNode> get(ID key) {
		Entry<ID, PeerNode> resultEntry = peerView.floorEntry(key);
		return ImmutableSet.of(resultEntry.getValue());
	}

	public void clear() {
		peerView.clear();
	}

	public Set<PeerNode> getPeerView() {
		return new HashSet<PeerNode>(peerView.values());
	}
}

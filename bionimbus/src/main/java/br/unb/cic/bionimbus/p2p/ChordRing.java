package br.unb.cic.bionimbus.p2p;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;


public final class ChordRing {

	public static final int SHA1_BIT_SIZE = 160;

	private final int m;

	private final PeerNode[] finger;
	private final ID id;
	private final PeerNode peer;

	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

//	private PeerNode predecessor;

	public ChordRing(PeerNode thisNode) {
		this(thisNode, SHA1_BIT_SIZE);
	}

	public ChordRing(PeerNode thisNode, int bitsize) {
		id = thisNode.getId();
		peer = thisNode;
		m  = bitsize;
		finger = new PeerNode[m];
		
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("chord").build();
		executor = Executors.newScheduledThreadPool(3, threadFactory);
		
	}
	
	public void start() {
		executor.scheduleAtFixedRate(new Gossiper(), 1, 2, TimeUnit.MINUTES);
	}
	
	public void stop() {
		executor.shutdownNow();
	}

	public synchronized PeerNode successor(ID key) {
		ID successor = successor().getId();
		if (key.gt(id) && key.lte(successor)){
			return successor();
		}
		else {
			PeerNode n = getClosestPrecedingNode(key);
			return n.retrieveSuccessor(key);
		}
	}

	public synchronized PeerNode getClosestPrecedingNode(ID key) {

		for (int i = finger.length; i >= 0; i--) {
			if (finger[i] != null){
				ID f = finger[i].getId();
				if (f.gt(id) && f.lt(key)){
					return finger[i];
				}
			}
		}
		return peer;
	}

	public synchronized PeerNode successor() {
		return finger[0];
	}

	public synchronized Collection<PeerNode> getRing() {
		return Arrays.asList(finger);
	}

	public synchronized int size() {
		int count = 0;
		for (int i = 0; i < finger.length; i++){
			if (finger[i] != null) {
				count++;
			}
		}
		return count;
	}

	public synchronized void insert(PeerNode peerNode) {

		ID candidate = peerNode.getId();

		for (int i = 0; i < finger.length; i++) {

			ID temp = id.add(ID.pow(i)).mod(m);

			if (candidate.gte(temp)){
				if (finger[i] == null)
					finger[i] = peerNode;
			}
			else {
				break;
			}
		}
	}

	public synchronized void remove(PeerNode peerNode) {
		for (int i = 0; i < finger.length; i++) {
			if (finger[i].equals(peerNode)){
				finger[i] = null;
			}
		}
	}

	public synchronized Collection<PeerNode> peers() {
		final SortedSet<PeerNode> peers = new TreeSet<PeerNode>();
		peers.addAll(Arrays.asList(finger));
		return peers;
	}

	public synchronized String printRawTable() {
		return getRing().toString();
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Table for ID " + id + ":");
		final SortedSet<ID> ids = new TreeSet<ID>();
		for (PeerNode p : finger){
			if (p != null)
				ids.add(p.getId());
		}
		sb.append(ids.toString());
		return sb.toString();
	}

	private class Gossiper implements Runnable {

		@Override
		public void run() {
			System.out.println("updating DHT tables");
		}
		
	}
}

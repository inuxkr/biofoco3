package br.unb.cic.bionimbus.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;


public interface ElectionStrategy {

	public String runElection(ZooKeeper zk, String ELECTION_DIR, String nodeData, Watcher watcher) throws KeeperException, InterruptedException;
}

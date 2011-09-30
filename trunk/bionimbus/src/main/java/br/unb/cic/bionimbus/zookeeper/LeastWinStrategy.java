package br.unb.cic.bionimbus.zookeeper;

import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;


public class LeastWinStrategy implements ElectionStrategy {

	@Override
	public String runElection(ZooKeeper zk, String ELECTION_DIR, String nodeData, Watcher watcher) throws KeeperException, InterruptedException {

		List<String> children = getChildren(zk, ELECTION_DIR);
		Collections.sort(children);
		
		String newLeader = null;
		Long anterior = Long.MAX_VALUE;
		for (String child : children) {
			if (child.startsWith("n_")){
				Long value = Long.parseLong(child.substring(2));
				if (value < anterior){
					newLeader = child;
					anterior = value;
				}
			}
		}
		
		return newLeader;

//		if (id.equals(newLeader)){
//			System.out.println("I (" + id + ") am the new leader ");
//			zk.create(ELECTION_DIR + "/leader", info.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//		}
//		else {
//			zk.exists(ELECTION_DIR + "/" + newLeader, this);
//		}

	}
	
	public List<String> getChildren(ZooKeeper zk, String ELECTION_DIR) throws KeeperException, InterruptedException {
		System.out.println(ELECTION_DIR);
		return zk.getChildren(ELECTION_DIR, false);
	}

}

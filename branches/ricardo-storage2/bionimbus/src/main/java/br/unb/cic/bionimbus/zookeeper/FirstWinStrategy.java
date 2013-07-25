package br.unb.cic.bionimbus.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


public class FirstWinStrategy implements ElectionStrategy {

	@Override
	public String runElection(ZooKeeper zk, String ELECTION_DIR, String nodeData, Watcher watcher) throws KeeperException, InterruptedException {

		// verifica existencia de znode 'leader'
		Stat stat = null;
		if ((stat = zk.exists(ELECTION_DIR + "/leader", watcher)) == null) {
			try {
				zk.create(ELECTION_DIR + "/leader", nodeData.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			} catch (KeeperException ke) {
				if (ke.code() != Code.NODEEXISTS) {
					throw ke;
				}
			}
		}

		byte[] leader = zk.getData(ELECTION_DIR + "/leader", true, stat);
		System.out.println("resultado da eleicao: " + new String(leader));
		return new String(leader);
	}

}

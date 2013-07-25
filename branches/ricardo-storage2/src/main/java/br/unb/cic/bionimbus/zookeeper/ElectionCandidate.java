
package br.unb.cic.bionimbus.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ElectionCandidate implements Watcher {

	private final ZooKeeper zk;

	private final String info;

	private String ELECTION_DIR = "/election";

	private String path;

	private String id;

	private static final int timeout = 3000;

	private static final int DEFAULT_PORT = 2181;

	// private static final Logger LOG =
	// Logger.getLogger(ElectionCandidate.class);

	public ElectionCandidate(String info, String address) throws IOException, KeeperException, InterruptedException {

		zk = new ZooKeeper(address + ":" + DEFAULT_PORT, timeout, null);
		this.info = info;

		System.out.println("I am node " + info);

		// verifica existencia de no' pai
		Stat s = zk.exists(ELECTION_DIR, false);
		if (s == null) {
			try {
				System.out.println("creating election directory");
				zk.create(ELECTION_DIR, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (KeeperException ke) {
				if (ke.code() != Code.NODEEXISTS) {
					throw ke;
				}
			}
		}

		System.out.println("creating sequence number");
		path = zk.create(ELECTION_DIR + "/n_", info.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		id = path.substring(path.lastIndexOf("/") + 1);

		System.out.println("my id is " + id);
	}

	public void runElection() throws IOException, KeeperException, InterruptedException {

		String leader = new FirstWinStrategy().runElection(zk, ELECTION_DIR, info, this);
		System.out.println("The leader is " + leader);
		
		System.out.println(new LeastWinStrategy().runElection(zk, ELECTION_DIR, info, this));

	}

	public String getLeader() throws KeeperException, InterruptedException {

		Stat stat = null;
		if ((stat = zk.exists(ELECTION_DIR + "/leader", this)) == null) {
			byte[] data = zk.getData(ELECTION_DIR + "/leader", true, stat);
			return new String(data);
		}
		return null;
	}

	@Override
	public void process(WatchedEvent event) {

		String path = event.getPath();
		Event.EventType type = event.getType();

		if ((type == Event.EventType.NodeDeleted) && path.equals(ELECTION_DIR + "/leader")) {
			System.out.println("Leader morreu, rode novo processo de eleição");
			try {
				runElection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() throws InterruptedException {
		zk.close();
	}
}

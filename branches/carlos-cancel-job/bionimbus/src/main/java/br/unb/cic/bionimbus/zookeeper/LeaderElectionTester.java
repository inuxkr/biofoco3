package br.unb.cic.bionimbus.zookeeper;
import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.KeeperException;

public class LeaderElectionTester {

	public static void main(String[] args) throws InterruptedException, IOException, KeeperException {

		String id = "localhost:" + new Random().nextInt(Integer.MAX_VALUE);
		new ElectionCandidate(id, "127.0.0.1").runElection();
		while (true) {}
	}
}

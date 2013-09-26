package br.unb.cic.bionimbus.services;

import br.unb.cic.bionimbus.services.ZooKeeperService;
import com.twitter.common.zookeeper.ZooKeeperClient;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ZooKeeperServiceTest {

    public static void main(String[] args) throws InterruptedException, ZooKeeperClient.ZooKeeperConnectionException, TimeoutException, IOException, KeeperException {
        ZooKeeperService zooKeeperService = new ZooKeeperService();
        zooKeeperService.connect("localhost:2181");

        for (int i = 0; i < 10; i++)
            zooKeeperService.createEphemeralSequentialZNode("/node", "Helo World");


        TimeUnit.SECONDS.sleep(30);

        zooKeeperService.delete("/this");

        System.out.println("bye bye");
    }
}

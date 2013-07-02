package br.unb.cic.bionimbus.zookeeper;

import br.unb.cic.bionimbus.network.utils.NetUtils;
import br.unb.cic.bionimbus.services.file.FileService;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: edward
 * To change this template use File | Settings | File Templates.
 */
public class ZooKeeperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperService.class);

    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT = 3000;
    private volatile boolean connecting = true;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);


    public void connect(String hosts) throws IOException, InterruptedException {
        System.out.println("Conectando ao ZK...");
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

                // Evento que indica conexão ao ensemble
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Conectado ao ZK!");
                    countDownLatch.countDown();
                }
                System.out.println(event);
            }
        });

        // Espera pelo evento de conexão
        countDownLatch.await();
    }

    public void createPersistentZNode(String root, String data) {

        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    System.out.println(String.format("znode %s não existe ... criando", root));
                    zk.create(root
                            , (data == null) ? new byte[0] : data.getBytes()
                            , ZooDefs.Ids.OPEN_ACL_UNSAFE // sem segurança
                            , CreateMode.PERSISTENT);
                } else {
                    System.out.println(String.format("znode %s existente", root));
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String createEphemeralSequentialZNode(final String path, String data) {
        String peer = null;
        byte[] buf = (data != null) ? data.getBytes() : new byte[0];
        try {
            peer = zk.create(path
                    , buf
                    , ZooDefs.Ids.OPEN_ACL_UNSAFE
                    , CreateMode.EPHEMERAL_SEQUENTIAL);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        return peer;
    }

    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zk.getChildren(path, watcher, null);
    }

    public String getData(String path, Watcher watcher) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(path, watcher, null);
        return new String(data);
    }

    public void setData(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = zk.setData(path, data.getBytes(), -1);
    }

    public void close() throws InterruptedException {
        zk.close();
    }
}
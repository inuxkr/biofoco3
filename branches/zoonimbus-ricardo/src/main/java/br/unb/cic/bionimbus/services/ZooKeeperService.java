package br.unb.cic.bionimbus.services;

import com.google.common.base.Preconditions;
import com.google.inject.Singleton;
import com.twitter.common.base.Command;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.common.zookeeper.ZooKeeperUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.zookeeper.KeeperException.ConnectionLossException;

@Singleton
public class ZooKeeperService {

    /**
     * URL: http://twitter.github.io/commons/apidocs/com/twitter/common/zookeeper/package-frame.html
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperService.class);

    private String hosts;
    private ZooKeeperClient zkClient;
    /*    private ZooKeeper zk;

        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        private volatile Status status = Status.NO_CONNECTED;
        public enum Status {
            NO_CONNECTED, CONNECTING, CONNECTED
        }*/
    private volatile Path path = Path.ROOT;
    private static final int SESSION_TIMEOUT = 5000;
    private static final Amount<Long, Time> CONNECTION_TIMEOUT = Amount.of(5L, Time.SECONDS);


    /**
     * Classe interna do ZookeeperService que possui, o método construtor Path(String value),
     * método getFullPath(String pluginid,String fileid,String taskid), que retorna o caminho completo do znode
     */
    public enum Path {

        ROOT("/"), PREFIX_PEER("/peer_"), PEERS("/peers"), FILES("/files"), PENDING_SAVE("/pending_save"), PREFIX_PENDING_FILE("/pending_file_"),
        JOBS("/jobs"), PREFIX_FILE("/file_"), STATUS("/STATUS"), STATUSWAITING("/STATUSWAITING"), SCHED("/sched"), LOCK_JOB("/LOCK"),
        SIZE_JOBS("/size_jobs"), TASKS("/tasks"), PREFIX_TASK("/task_"), PREFIX_JOB("/job_"), UNDERSCORE("_");

        private final String value;

        private Path(String value) {
            this.value = value;
        }

        /**
         * @param pluginid Os Enums, PREFIX_PEER, STATUS, STATUSWAITING, SCHED, SIZE_JOBS, TASKS, PREFIX_TASK, FILES, PREFIX_FILE utilizam como parametro
         * @param fileid   OS enums, PREFIX_PENDING_FILE, PREFIX_FILE, utilizam o id dos file
         * @param taskid   Os enums, PREFIX_TASK, utilizam
         * @return
         */
        public String getFullPath(String pluginid, String fileid, String taskid) {
            switch (this) {
                case ROOT:
                    return "" + this;
                case PENDING_SAVE:
                    return "" + PENDING_SAVE;
                case PREFIX_PENDING_FILE:
                    return "" + PENDING_SAVE + PREFIX_PENDING_FILE + fileid;
                case JOBS:
                    return "" + JOBS;
                case PREFIX_JOB:
                    return "" + JOBS + PREFIX_JOB + taskid;
                case PEERS:
                    return "" + PEERS;
                case PREFIX_PEER:
                    return "" + PEERS + PREFIX_PEER + pluginid;
                case STATUS:
                    return "" + PEERS + PREFIX_PEER + pluginid + STATUS;
                case STATUSWAITING:
                    return "" + PEERS + PREFIX_PEER + pluginid + STATUSWAITING;
                case SCHED:
                    return "" + PEERS + PREFIX_PEER + pluginid + SCHED;
                case SIZE_JOBS:
                    return "" + PEERS + PREFIX_PEER + pluginid + SCHED + SIZE_JOBS;
                case TASKS:
                    return "" + PEERS + PREFIX_PEER + pluginid + SCHED + TASKS;
                case PREFIX_TASK:
                    return "" + PEERS + PREFIX_PEER + pluginid + SCHED + TASKS + PREFIX_TASK + taskid;
                case FILES:
                    return "" + PEERS + PREFIX_PEER + pluginid + FILES;
                case PREFIX_FILE:
                    return "" + PEERS + PREFIX_PEER + pluginid + FILES + PREFIX_FILE + fileid;
                case LOCK_JOB:
                    return "" + JOBS + PREFIX_JOB + taskid + LOCK_JOB;
            }
            return "";
        }

        public String getCodigo() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public ZooKeeperService() {
        LOGGER.info("Criando ZK service...");
    }

    public Path getPath() {
        return path;
    }
    
/*

    public Status getStatus() {
        return status;
    }
    
    public synchronized void reconnect() throws IOException, InterruptedException {
        LOGGER.info("Reconectando em hosts " + hosts);
        status = Status.NO_CONNECTED;
        connect(hosts);
    }*/


    public synchronized void connect(String hosts) throws IOException, InterruptedException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {

        this.hosts = hosts;

        Preconditions.checkNotNull(hosts, "zkHosts cannot be null");

        LOGGER.info("Conectando ao ZK...");
        zkClient = new ZooKeeperClient(Amount.of(SESSION_TIMEOUT, Time.MILLISECONDS), asIterable(hosts));
        zkClient.registerExpirationHandler(new Command() {
            @Override
            public void execute() throws RuntimeException {
                System.out.println("ZK SESSION EXPIRED!!!!!");
            }
        });
        zkClient.get(CONNECTION_TIMEOUT);

        LOGGER.debug("Conectado!");
    }

    private Iterable<InetSocketAddress> asIterable(String hosts) {
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

        for (String s : hosts.split(",")) {
            String[] hostport = s.trim().split(":");

            addresses.add(new InetSocketAddress(hostport[0], Integer.parseInt(hostport[1])));
        }
        return addresses;
    }

    private ZooKeeper zk() throws InterruptedException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {
         return zkClient.get(CONNECTION_TIMEOUT);
    }

    private String createZNode(String path, String data, CreateMode mode)  {
        if (zkClient == null)
            throw new IllegalStateException("ZooKeeperService is not connected");

        String peer = null;
        try {
            String parentPath = path.substring(0, path.lastIndexOf("/"));
            if (parentPath.length() > 0)
                ZooKeeperUtils.ensurePath(zkClient, ZooDefs.Ids.OPEN_ACL_UNSAFE, parentPath);
            Stat s = zk().exists(path, false);

            if (s == null) {
//                    System.out.println(String.format("znode %s não existe ... criando", root));
                peer = zkClient.get(CONNECTION_TIMEOUT).create(path
                        , (data == null) ? new byte[0] : data.getBytes()
                        , ZooDefs.Ids.OPEN_ACL_UNSAFE // sem segurança
                        , mode);
            } else {
//                    System.out.println(String.format("znode %s existente", root));
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return peer;
    }

    /**
     * Cria um znode persistent
     *
     * @param path Path do znode
     * @param data Dado a ser gravado no znode
     * @return
     */
    public String createPersistentZNode(String path, String data) {
        return createZNode(path, data, CreateMode.PERSISTENT);
    }

    /**
     * @param path
     * @param data
     * @return
     */
    public String createPersistentSequentialZNode(String path, String data) {
        return createZNode(path, data, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    /**
     * @param path
     * @param data
     * @return
     */
    public String createEphemeralZNode(final String path, String data) {
        return createZNode(path, data, CreateMode.EPHEMERAL);
    }

    /**
     * @param path
     * @param data
     * @return
     */
    public String createEphemeralSequentialZNode(final String path, String data) {
        return createZNode(path, data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * @param path
     * @param watch
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean getZNodeExist(String path, boolean watch) throws KeeperException, InterruptedException, ZooKeeperClient.ZooKeeperConnectionException, TimeoutException {

        Stat stat = zk().exists(path, watch);
        return (stat == null) ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * @param path
     * @param watcher
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException, IOException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {

        List<String> retorno = zk().getChildren(path, watcher, null);
        return retorno;

    }

    /**
     * @param path
     * @param watcher
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getData(String path, Watcher watcher) throws KeeperException, InterruptedException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {
        byte[] data = zk().getData(path, watcher, null);
        return new String(data);
    }

    /**
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void setData(String path, String data) throws KeeperException, InterruptedException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        ZooKeeperUtils.ensurePath(zkClient, ZooDefs.Ids.OPEN_ACL_UNSAFE, parentPath);
        zk().setData(path, data.getBytes(), -1);
    }

    /**
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void delete(String path) throws KeeperException, InterruptedException, TimeoutException, ZooKeeperClient.ZooKeeperConnectionException {
        List<String> children = zkClient.get(CONNECTION_TIMEOUT).getChildren(path, null);
        for (String child : children) {
            delete(path + "/" + child);
        }
        zk().delete(path, -1);
    }

    /**
     * Método que fecha a conexão com o zookeeper
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        zkClient.close();
    }
}

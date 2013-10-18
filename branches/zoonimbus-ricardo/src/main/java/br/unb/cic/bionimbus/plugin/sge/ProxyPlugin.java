package br.unb.cic.bionimbus.plugin.sge;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.AbstractPlugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.services.ZooKeeperService;

public class ProxyPlugin extends AbstractPlugin {

    private ServerSocket server;
    private static final int PORT = 8080;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private volatile boolean ready;

    public ProxyPlugin(P2PService p2p) throws IOException {
        super(p2p);
    }

    public void startServer() throws IOException {
        server = new ServerSocket(PORT);
        while (true) {
            Socket client = server.accept();
        }
    }

    @Override
    protected Future<PluginInfo> startGetInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Future<PluginFile> saveFile(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Future<PluginGetFile> getFile(Host origin, PluginFile file, String taskId, String savePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<PluginTask> startTask(PluginTask task, ZooKeeperService zk) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isReady() {
        return ready;
    }

	@Override
	public File[] listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getFile(String file) {
		// TODO Auto-generated method stub
		
	}

}
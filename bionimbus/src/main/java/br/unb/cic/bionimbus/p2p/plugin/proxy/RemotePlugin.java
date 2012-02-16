package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.ID;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.AbstractPlugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class RemotePlugin extends AbstractPlugin {

	private final ProxyServerStub server;

	private final Host host;

	private final String id;

	private final ExecutorService executor;

	public RemotePlugin(P2PService p2p, ExecutorService executor) {
		
		super(p2p);
		
		this.executor = executor;
		
		host = p2p.getPeerNode().getHost();
		id = p2p.getPeerNode().getId().toString();
		
		server = new ProxyServerStub(executor);		
		server.start();
	}

	@Override
	protected Future<PluginInfo> startGetInfo() {
		
		FutureTask<PluginInfo> future = new FutureTask<PluginInfo>(new Callable<PluginInfo>() {
			
			public PluginInfo call() throws Exception {
				
				while (!server.hasClientConnection()) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {	/* ignore */ }
				}
				
				server.request("INFO");				
				String response = server.response("INFO");
			
				
				ObjectMapper mapper = new ObjectMapper();
				PluginInfo info = mapper.readValue(response, PluginInfo.class);
				info.setId(id);
				info.setHost(host);
				
				return info;
				
			}
		});
        executor.execute(future);
        return future;
	}

	@Override
	protected Future<PluginFile> saveFile(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Future<PluginGetFile> getFile(Host origin, PluginFile file,
			String taskId, String savePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Future<PluginTask> startTask(PluginTask task) {
		// TODO Auto-generated method stub
		return null;
	}

}

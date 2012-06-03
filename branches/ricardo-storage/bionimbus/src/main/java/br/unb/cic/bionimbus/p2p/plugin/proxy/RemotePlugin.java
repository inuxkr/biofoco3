package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.io.Files;

import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.AbstractPlugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
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

		FutureTask<PluginInfo> future = new FutureTask<PluginInfo>(
				new Callable<PluginInfo>() {

					public PluginInfo call() throws Exception {

						while (!server.hasClientConnection()) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) { /* ignore */
							}
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
	protected Future<PluginFile> saveFile(final String filename) {
		FutureTask<PluginFile> future = new FutureTask<PluginFile>(
				new Callable<PluginFile>() {

					public PluginFile call() throws Exception {

						while (!server.hasClientConnection()) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) { /* ignore */
							}
						}

//						Hashifier.hashContent(new File(filename))
						
						System.out.println("Transfering file ...");
											
						server.request("SAVE-FILE" + "#" + filename + "#", new File(filename));
						
						System.out.println("Finished file " + filename);
												
						String response = server.response("SAVE-FILE");

						ObjectMapper mapper = new ObjectMapper();
						PluginFile info = mapper.readValue(response, PluginFile.class);

						return info;

					}
				});
		executor.execute(future);
		return future;
	}

	@Override
	protected Future<PluginGetFile> getFile(final Host origin,
			final PluginFile pluginFile, final String taskId,
			final String filename) {

		FutureTask<PluginGetFile> future = new FutureTask<PluginGetFile>(
				new Callable<PluginGetFile>() {

					public PluginGetFile call() throws Exception {

						PluginGetFile getFile = new PluginGetFile();
						getFile.setPeer(origin);
						getFile.setPluginFile(pluginFile);
						getFile.setTaskId(taskId);

						server.request("GET-FILE" + "#" + filename, new File(filename));

//						executor.submit(new Runnable() {
//
//							@Override
//							public void run() {					
//																
//								try {																						
////									int port = rollover.next();
//									int port = 8181;
//									System.out.println("Allocating port " + port + " to transfer file");
//									
//									System.out.println("Transfering file ...");
//									FileTransferServer server = new FileTransferServer(port, filename);
//									server.receive();							
//									System.out.println("Finished file " + filename);
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (NoSuchAlgorithmException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}								
//							}
//								
//						});											
						
						String response = server.response("GET-FILE");

						ObjectMapper mapper = new ObjectMapper();
						PluginGetFile info = mapper.readValue(response, PluginGetFile.class);

						return info;
					}
				});
		executor.execute(future);
		return future;
	}
	
//	public void transfer(String file, String remotePath) throws IOException {
//		FileServer server = new FileServer();
//		
//	}

	@Override
	protected Future<PluginTask> startTask(PluginTask task) {
		PluginService service = getMyInfo().getService(task.getJobInfo().getServiceId());
		
		if (service == null)
			return null;
		
		throw new UnsupportedOperationException();
	}

}

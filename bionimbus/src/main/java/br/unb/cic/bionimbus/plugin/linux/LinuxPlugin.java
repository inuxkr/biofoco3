package br.unb.cic.bionimbus.plugin.linux;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.AbstractPlugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskRunner;

public class LinuxPlugin extends AbstractPlugin {

	private final ExecutorService executorService = Executors.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("LinuxPlugin-workers-%d").build());

	public LinuxPlugin(final P2PService p2p) {
		super(p2p);
	}

	@Override
	protected Future<PluginInfo> startGetInfo() {
		return executorService.submit(new LinuxGetInfo());
	}

	@Override
	protected Future<PluginFile> saveFile(String filename) {
		return executorService.submit(new LinuxSaveFile(filename));
	}

	@Override
	protected Future<PluginGetFile> getFile(Host origin, PluginFile file, String taskId, String savePath) {
		return executorService.submit(new LinuxGetFile(file, taskId, origin, savePath));
	}

	@Override
	protected Future<PluginTask> startTask(PluginTask task) {
		PluginService service = getMyInfo().getService(task.getJobInfo().getServiceId());
		if (service == null)
			return null;

		return executorService.submit(new PluginTaskRunner(this, task, service, getP2P().getConfig().getServerPath()));
	}

}

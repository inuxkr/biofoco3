package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.services.ZooKeeperService;
import br.unb.cic.bionimbus.services.storage.StorageService;
import br.unb.cic.bionimbus.utils.Compactacao;
import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.plugin.AbstractPlugin;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskRunner;

public class HadoopPlugin extends AbstractPlugin {
    
    private final ExecutorService executorService = Executors.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("HadoopPlugin-workers-%d").build());
    
    public HadoopPlugin(final P2PService p2p) throws IOException {
        super(p2p);
    }
    
    public HadoopPlugin() throws IOException{
        this(null);
    }

    @Override
    protected Future<PluginInfo> startGetInfo() {
        return executorService.submit(new HadoopGetInfo());
    }

    @Override
    protected Future<PluginFile> saveFile(String filename) {
        return executorService.submit(new HadoopSaveFile(filename));
    }

    @Override
    protected Future<PluginGetFile> getFile(Host origin, PluginFile file, String taskId, String savePath) {
        return executorService.submit(new HadoopGetFile(file, taskId, origin, savePath));
    }

    @Override
    public Future<PluginTask> startTask(PluginTask task, ZooKeeperService zk) {
        PluginService service = getMyInfo().getService(task.getJobInfo().getServiceId());
        if (service == null)
            return null;

        return executorService.submit(new PluginTaskRunner(this, task, service, getP2P().getConfig().getServerPath(),zk));
    }

	@Override
	public File[] listFiles() {
        Process p = null;
        List<String> lista = new ArrayList<String>();
        try {
            p = Runtime.getRuntime().exec("/home/ubuntu/hadoop-1.0.3/bin/hadoop fs -ls");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
            	if (line.subSequence(0, 1).equals("-"))
            		lista.add(line.substring(line.lastIndexOf("/")+1));
            }
            br.close();
            
            File[] files = new File[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
            	files[i] = new File(lista.get(i));	
			}
            
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }

		return null;
	}

	@Override
	public void getFile(String file) {
		PluginFile pluginFile = new PluginFile();
		pluginFile.setName(file);
		pluginFile.setPath(file);
		executorService.submit(new HadoopGetFile(pluginFile, null, null, StorageService.DATAFOLDER));
	}

}
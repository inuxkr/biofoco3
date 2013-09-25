package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginGetFile;
import br.unb.cic.bionimbus.services.storage.StorageService;
import br.unb.cic.bionimbus.utils.Compactacao;

public class HadoopGetFile implements Callable<PluginGetFile> {

	private final PluginGetFile getFile;

	private final String serverPath;

    public HadoopGetFile(PluginFile pluginFile, String taskId, Host receiver, String serverPath) {
    	getFile = new PluginGetFile();
        getFile.setPeer(receiver);
        getFile.setPluginFile(pluginFile);
        getFile.setTaskId(taskId);
        this.serverPath = serverPath;
    }
    
    @Override
    public PluginGetFile call() throws Exception {
    	File file = new File(serverPath + File.separator + getFile.getPluginFile().getPath());
        Process p = null;
        
        try {
            p = Runtime.getRuntime().exec("/home/ubuntu/hadoop-1.0.3/bin/hadoop fs -get " + getFile.getPluginFile().getPath() + " " + StorageService.DATAFOLDER+file.getName());
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		String path = StorageService.DATAFOLDER+file.getName();
		try {
			Compactacao.compactar(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

        return getFile;
    }

}

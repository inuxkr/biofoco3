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
import br.unb.cic.bionimbus.utils.Propriedades;
import br.unb.cic.bionimbus.utils.Utilities;

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
		String path = StorageService.DATAFOLDER+file.getName();
    	File file2 = new File(path);
    	if (!file2.exists()) {
	        Process p = null;
	        
	        try {
	        	System.out.println("Arquivo " + file.getName() + " inicio download hadoop " + Utilities.getDateString());
	            p = Runtime.getRuntime().exec("/home/ubuntu/hadoop-1.0.3/bin/hadoop fs -get " + getFile.getPluginFile().getPath() + " " + StorageService.DATAFOLDER+file.getName());
	            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	            String line;
	            while ((line = br.readLine()) != null) {
	                System.out.println(line);
	            }
	            br.close();
	            System.out.println("Arquivo " + file.getName() + " termino download hadoop " + Utilities.getDateString());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        if (Propriedades.getProp("storage.compact").equals("ẗrue"))  {
				try {
					System.out.println("Arquivo " + file.getName() + " inicio compactação " + Utilities.getDateString());
					Compactacao.compactar(path);
					System.out.println("Arquivo " + file.getName() + " termino compactação " + Utilities.getDateString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
    	}
    	
        return getFile;
    }

}

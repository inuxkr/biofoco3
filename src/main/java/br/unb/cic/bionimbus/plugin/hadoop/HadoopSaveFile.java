package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.plugin.PluginFile;

public class HadoopSaveFile implements Callable<PluginFile> {
	
	private final String filePath;
	
	public HadoopSaveFile(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public PluginFile call() throws Exception {
		File file = new File(filePath);
		Process p = null;
		PluginFile pFile = null;
		
		try {
			p = Runtime.getRuntime().exec("hadoop fs -moveFromLocal " + filePath + " " + file.getName());
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			
			pFile = new PluginFile();
			pFile.setPath(file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return pFile;
	}

}

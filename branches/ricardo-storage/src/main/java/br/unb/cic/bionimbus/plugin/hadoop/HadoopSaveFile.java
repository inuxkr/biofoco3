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
		PluginFile pFile;
		
		try {
			pFile = new PluginFile();
			pFile.setPath(file.getName());
			pFile.setSize(file.length());

			System.out.println("hadoop fs -moveFromLocal " + filePath + " " + file.getName());
			p = Runtime.getRuntime().exec("/usr/local/hadoop/bin/hadoop fs -moveFromLocal " + filePath + " " + file.getName());
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			pFile = null;
		}
		
		
		return pFile;
	}

}
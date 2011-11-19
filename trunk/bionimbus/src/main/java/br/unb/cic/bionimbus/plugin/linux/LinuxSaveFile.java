package br.unb.cic.bionimbus.plugin.linux;

import java.io.File;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.plugin.PluginFile;

public class LinuxSaveFile implements Callable<PluginFile> {

	private final String filePath;
	
	public LinuxSaveFile(String filePath) {
		this.filePath = filePath;
	}
	
	public PluginFile call() throws Exception {
		File file = new File(filePath);
		PluginFile pFile = new PluginFile();
		pFile.setPath(file.getName());
		pFile.setSize(file.length());
		file.renameTo(new File(LinuxGetInfo.PATH + File.pathSeparator + file.getName()));
		return pFile;
	}
}

package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.plugin.PluginService;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;

public class HadoopTask implements Callable<PluginTask> {

	private PluginTask task = null;
	
	private PluginService service = null;

	public HadoopTask(PluginService service, PluginTask task) {
		this.service = service;
		this.task = task;
	}

	@Override
	public PluginTask call() throws Exception {

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(service.getPath());
			task.setState(PluginTaskState.RUNNING);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			task.setState(PluginTaskState.DONE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return task;
	}

}

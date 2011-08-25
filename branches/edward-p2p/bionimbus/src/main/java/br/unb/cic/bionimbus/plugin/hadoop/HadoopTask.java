package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginService;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;

public class HadoopTask implements Callable<PluginTask> {
	
	private JobInfo job = null;

	private PluginTask task = null;
	
	private PluginService service = null;

	public HadoopTask(JobInfo job, PluginService service, PluginTask task) {
		this.service = service;
		this.task = task;
		this.job = job;
	}

	@Override
	public PluginTask call() throws Exception {
		
		String args = "";
		for (String arg : job.getArgs()) {
			args += (" " + arg);
		}

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(service.getPath() + args);
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

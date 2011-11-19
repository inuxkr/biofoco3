package br.unb.cic.bionimbus.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class PluginTaskRunner implements Callable<PluginTask> {

	private final AbstractPlugin plugin;

	private final PluginTask task;

	private final PluginService service;

	private final String path;

	public PluginTaskRunner(AbstractPlugin plugin, PluginTask task,
			PluginService service, String path) {
		this.plugin = plugin;
		this.service = service;
		this.task = task;
		this.path = path;
	}

	@Override
	public PluginTask call() throws Exception {

		String args = task.getJobInfo().getArgs();
		Set<String> inputs = task.getJobInfo().getInputs().keySet();
		int i = 1;
		for (String input : inputs) {
			args = args.replaceFirst("%I" + i, path + "/" + plugin.getInputFiles().get(input).first);
			i++;
		}

		List<String> outputs = task.getJobInfo().getOutputs();
		i = 1;
		for (String output : outputs) {
			args = args.replaceFirst("%O" + i, path + "/" + output);
			i++;
		}

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(service.getPath() + " " + args);
			task.setState(PluginTaskState.RUNNING);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
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

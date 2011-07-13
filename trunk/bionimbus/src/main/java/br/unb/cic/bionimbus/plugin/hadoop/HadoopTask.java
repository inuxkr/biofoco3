package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;

public class HadoopTask implements Callable<PluginTask> {

	private static final String path = "/home/hugo.saldanha/Documents/projetos/bionimbus/iaas/hadoop/tools/hadoop-0.20.203.0/apps/";
	
	private PluginTask task = null;

	public HadoopTask(PluginTask task) {
		this.task = task;
	}

	@Override
	public PluginTask call() throws Exception {
		// baixar arquivos de entrada
		// executar a tarefa e salvar ID para futuras consultas ao hadoop
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(path + "test.sh");
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

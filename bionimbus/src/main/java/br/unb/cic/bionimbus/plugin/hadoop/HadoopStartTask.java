package br.unb.cic.bionimbus.plugin.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class HadoopStartTask implements Callable<Boolean> {

	private static final String path = "/home/hugo.saldanha/Documents/projetos/bionimbus/iaas/hadoop/tools/hadoop-0.20.203.0/apps/";

	public HadoopStartTask(/* TaskInfo info */) {
		// salva referencia para informacoes da tarefa
	}

	@Override
	public Boolean call() throws Exception {
		// baixar arquivos de entrada
		// executar a tarefa e salvar ID para futuras consultas ao hadoop
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(path + "test.sh");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

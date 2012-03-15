package br.unb.cic.bionimbus.client.experiments;

import java.util.ArrayList;
import java.util.List;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginFile;

public class MscPipeline implements Pipeline {
	
	private int stage = -1;
	
	private final PluginFile input;
	
	private List<JobInfo> jobs = new ArrayList<JobInfo>();
	
	private List<String> outputFiles = new ArrayList<String>();
	
	public MscPipeline(PluginFile file) {
		input = file;
		outputFiles.add("data.txt");
		
		JobInfo job = new JobInfo();
		job.setArgs("%O1");
		job.setId(null);
		job.setServiceId(1111);
		job.addInput(input.getId(), input.getSize());
		job.addOutput(outputFiles.get(0));
		jobs.add(job);
	}

	@Override
	public String getCurrentOutput() {
		return outputFiles.get(stage);
	}

	@Override
	public JobInfo nextJob() {
		stage++;
		JobInfo job = null;
		if (stage < jobs.size())
			job = jobs.get(stage);
		return job;
	}

	@Override
	public String getInput() {
		return input.getPath();
	}
}

package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.List;

import Jama.Matrix;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;

public class AHPPolicy extends SchedPolicy {

	private List<JobInfo> jobs;
	
	@Override
	public PluginInfo schedule(JobInfo jobInfo) throws SchedException {
		jobs.add(jobInfo);
		return null;
	}
	
	private Matrix inducedMatrix(Matrix matrix, double n) {
		return matrix.arrayTimes(matrix).minus(matrix.times(n));
	}

}

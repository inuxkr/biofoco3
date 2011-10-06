package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;

public class AHPPolicy extends SchedPolicy {

	private List<JobInfo> jobs;
	
	public void addJob(JobInfo jobInfo) throws SchedException {
		jobs.add(jobInfo);
	}
	
	@Override
	public PluginInfo schedule(JobInfo jobInfo) throws SchedException {
		
		
		return null;
	}
	
	public static float comparePluginInfo(PluginInfo a, PluginInfo b, String attribute) throws SchedException {
		double valueA = 0.0;
		double valueB = 0.0;
		
		if (attribute.equals("latency")) {
			// Tem que ser inverso ja que no caso de latency quanto menor melhor!
			valueA = b.getLatency();
			valueB = a.getLatency();
		} else if (attribute.equals("uptime")) {
			valueA = a.getUptime();
			valueB = b.getUptime();
		} else {
			throw new SchedException("Atributo não encontrado.");
		}
		
		double result = valueA / valueB;
		if (result == 0.0) {
			return 1;
		} else if (result < 1.0) {
			return (float) 1 / Math.round(valueB / valueA);
		} else if (result > 9) {
			return 9;
		} else {
			return Math.round(valueA / valueB);
		}
	}
	
	public static Matrix generateComparisonMatrix(List<PluginInfo> pluginInfos, String attribute) throws SchedException {
		Matrix m = new Matrix(pluginInfos.size(), pluginInfos.size());
		
		for (int i = 0; i < pluginInfos.size(); ++i) {
			for (int j = 0; j < pluginInfos.size(); ++j) {
				m.set(i, j, comparePluginInfo(pluginInfos.get(i), pluginInfos.get(j), attribute));
			}
		}
		return m;
	}
	
	public static List<Double> getPrioritiesOnMatrix(Matrix m) {
		List<Double> priorities = new ArrayList<Double>();
		double sum = 0.0;
		
		for (int i = 0; i < m.getColumnDimension(); ++i) {
			for (int j = 0; j < m.getRowDimension(); ++j) {
				sum += m.get(i, j);
			}
		}
		
		for (int i = 0; i < m.getColumnDimension(); ++i) {
			double pSum = 0.0;
			for (int j = 0; j < m.getRowDimension(); ++j) {
				pSum += m.get(i, j);
			}
			priorities.add(pSum / sum);
		}
		return priorities;
	}
	
	private static int getMaxNumberIndex(List<Double> numbers) {
		int maxIndex = 0;
		
		for (int i = 0; i < numbers.size(); ++i) {
			if (numbers.get(i) > numbers.get(maxIndex)) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public static List<PluginInfo> getServiceOrderedByPriority(List<PluginInfo> pluginInfos) throws SchedException {
		List<PluginInfo> plugins = new ArrayList<PluginInfo>();
		Matrix m = generateComparisonMatrix(pluginInfos, "latency");
		List<Double> priorities = getPrioritiesOnMatrix(m);
		
		while (!priorities.isEmpty()) {
			int index = getMaxNumberIndex(priorities);
			plugins.add(pluginInfos.get(index));
			pluginInfos.remove(index);
			priorities.remove(index);
		}
		return plugins;
	}
	
	private List<PluginInfo> filterByService(long serviceId) throws SchedException {
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		for (PluginInfo pluginInfo : getCloudMap().values()) {
			if (pluginInfo.getService(serviceId) != null)
				plugins.add(pluginInfo);
		}
		
		if (plugins.size() == 0) {
			throw new SchedException("Service not available.");
		}
		
		return plugins;
	}
	
	public Matrix inducedMatrix(Matrix matrix, double n) {
		return matrix.arrayTimes(matrix).minus(matrix.times(n));
	}

	
}

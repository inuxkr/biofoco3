package br.unb.cic.bionimbus.sched.policy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import Jama.Matrix;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;

public class AHPPolicy extends SchedPolicy {
	
    private List<PluginInfo> usedResources = new ArrayList<PluginInfo>();
	
	@Override
	public HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos) {
		HashMap<JobInfo, PluginInfo> jobMap = new HashMap<JobInfo, PluginInfo>();
		JobInfo biggerJob = getBiggerJob(new ArrayList<JobInfo>(jobInfos));
		jobMap.put(biggerJob, this.scheduleJob(biggerJob));
		return jobMap;
	}

	private PluginInfo scheduleJob(JobInfo jobInfo) {
		List<PluginInfo> plugins = filterByService(jobInfo.getServiceId(),
				getCloudMap().values());
		if (plugins.size() == 0) {
			return null;
		}
		return getBestService(plugins);
	}
	
	public static JobInfo getBiggerJob(List<JobInfo> jobInfos) {
		if (jobInfos.size() == 0)
			return null;
		JobInfo bigger = null;
		long biggerTotal = 0L;
		for (JobInfo jobInfo : jobInfos) {
			long total = getTotalSizeOfJobsFiles(jobInfo);
			if (bigger == null) {
				bigger = jobInfo;
				biggerTotal = total;
			} else {
				if (getTotalSizeOfJobsFiles(jobInfo) > biggerTotal) {
					bigger = jobInfo;
					biggerTotal = total;
				}
			}
		}
		return bigger;
	}

	public static long getTotalSizeOfJobsFiles(JobInfo jobInfo) {
		long sum = 0;
		for (long i : jobInfo.getInputs().values()) {
			sum += i;
		}
		return sum;
	}

	public static float comparePluginInfo(PluginInfo a, PluginInfo b,
			String attribute) {
		float valueA = 0.0f;
		float valueB = 0.0f;

		if (attribute.equals("latency")) {
			// Tem que ser inverso ja que no caso de latency quanto menor
			// melhor!
			valueA = b.getLatency();
			valueB = a.getLatency();
		} else if (attribute.equals("uptime")) {
			valueA = a.getUptime();
			valueB = b.getUptime();
		} else if (attribute.equals("processor")) {
			if (b.getNumOccupied() == 0 && a.getNumOccupied() == 0) {
				valueA = (float) 1.0 / b.getNumCores();
				valueB = (float) 1.0 / a.getNumCores(); 
			} else {
				valueA = (float) b.getNumOccupied() / b.getNumCores();
				valueB = (float) a.getNumOccupied() / a.getNumCores();
			}
		} else {
			throw new RuntimeException("Atributo " + attribute
					+ " não encontrado para escalonamento.");
		}

		if (valueA == 0.0 && valueB == 0.0) {
			return 1.0f;
		}
		
		if (valueB == 0.0) {
			return Float.MAX_VALUE;
		}
		
		return valueA / valueB;
		
		//System.out.println(attribute + ": " + valueA + " , " + valueB);
		/*
		double result;
		if (valueA == 0.0 && valueB == 0.0) {
			return 1;
		} else if (valueA == 0.0) {
			return 1;
		} else if (valueB == 0.0) {
			return 9;
		} else {
			result = valueA / valueB;
		}
		
		if (result == 0.0) {
			return 1;
		} else if (result < 1.0) {
			return (float) 1 / Math.round(Math.ceil(valueB / valueA));
		} else if (result > 9) {
			return 9;
		} else {
			//return Math.ceil
			return Math.round(Math.ceil(result));
		}
		*/
	}

	public static Matrix generateComparisonMatrix(List<PluginInfo> pluginInfos,
			String attribute) {
		Matrix m = new Matrix(pluginInfos.size(), pluginInfos.size());

		for (int i = 0; i < pluginInfos.size(); ++i) {
			for (int j = 0; j < pluginInfos.size(); ++j) {
				m.set(i,
						j,
						comparePluginInfo(pluginInfos.get(i),
								pluginInfos.get(j), attribute));
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
	
	public static List<Double> multiplyVectors(List<Double> a, List<Double> b) {
		List<Double> result = new ArrayList<Double>();

		if (a.size() != b.size())
			throw new RuntimeException(
					"Vetores sendo multiplicados possuem tamanhos diferentes no escalonamento");
		for (int i = 0; i < a.size(); ++i) {
			result.add(a.get(i) * b.get(i));
		}
		return result;
	}

	private static boolean allCoresOccupied(List<PluginInfo> pluginInfos) {
		for (PluginInfo pluginInfo : pluginInfos) {
			if (pluginInfo.getNumCores() > pluginInfo.getNumOccupied()) {
				return false;
			}
		}
		return true;
	}
	
	 private List<PluginInfo> filterByUsed() {
     	ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
     	for (PluginInfo pluginInfo : getCloudMap().values()) {
			if (!usedResources.contains(pluginInfo))
				plugins.add(pluginInfo);
     	}
     
     	if (plugins.size() == 0) {
			usedResources.clear();
			return new ArrayList<PluginInfo>(getCloudMap().values());
     	}
     
     	return plugins;
     }
	
	public List<PluginInfo> getServiceOrderedByPriority(
			List<PluginInfo> pluginInfos) {
		
		List<PluginInfo> plugins = new ArrayList<PluginInfo>();
		Matrix mLatency = generateComparisonMatrix(pluginInfos, "latency");
		Matrix mProcessor = generateComparisonMatrix(pluginInfos, "processor");
		List<Double> prioritiesLatency = getPrioritiesOnMatrix(mLatency);
		List<Double> prioritiesProcessor = getPrioritiesOnMatrix(mProcessor);
		List<Double> priorities = multiplyVectors(prioritiesLatency, prioritiesProcessor);
		
		while (!priorities.isEmpty()) {
			int index = getMaxNumberIndex(priorities);
			plugins.add(pluginInfos.get(index));
			pluginInfos.remove(index);
			priorities.remove(index);
		}
		return plugins;
	}

	public PluginInfo getBestService(List<PluginInfo> pluginInfos) {
		if (pluginInfos.isEmpty())
			return null;
		
		PluginInfo resource;
		if (allCoresOccupied(pluginInfos)) {
			resource = filterByUsed().get(0);
		} else {
			resource = getServiceOrderedByPriority(pluginInfos).get(pluginInfos.size());
		}
		usedResources.add(resource);
		return resource;
	}

	private List<PluginInfo> filterByService(long serviceId,
			Collection<PluginInfo> plgs) {
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		for (PluginInfo pluginInfo : plgs) {
			if (pluginInfo.getService(serviceId) != null)
				plugins.add(pluginInfo);
		}

		return plugins;
	}

	public Matrix inducedMatrix(Matrix matrix, double n) {
		return matrix.arrayTimes(matrix).minus(matrix.times(n));
	}
}

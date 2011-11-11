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
        
        private List<PluginInfo> usedResources;
        
        public void addJob(JobInfo jobInfo) throws SchedException {
                jobs.add(jobInfo);
        }
        
        public PluginInfo scheduleJob(JobInfo jobInfo) throws SchedException {
        	List<PluginInfo> plugins = filterByService(jobInfo.getServiceId(), filterByUsed());
            return getBestService(plugins);
        }
        
        @Override
        public PluginInfo schedule(JobInfo... jobInfos) throws SchedException {
        	PluginInfo resource = this.scheduleJob(jobInfos[0]);
        	usedResources.add(resource);
       		return resource;
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
                } else if (attribute.equals("occupied")) {
                    valueA = b.getNumOccupied();
                    valueB = a.getNumOccupied();
                } else if (attribute.equals("cores")) {
                    valueA = a.getNumCores();
                    valueB = b.getNumCores();    
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
        
        public static List<Double> multiplyVectors(List<Double> a, List<Double> b) throws SchedException {
                List<Double> result = new ArrayList<Double>();
                
                if (a.size() != b.size()) throw new SchedException("Vetores sendo multiplicados possuem tamanhos diferentes");
                for (int i = 0; i < a.size(); ++i) {
                        result.add(a.get(i) * b.get(i));
                }
                return result;
        }
        
        public static List<PluginInfo> getServiceOrderedByPriority(List<PluginInfo> pluginInfos) throws SchedException {
                List<PluginInfo> plugins = new ArrayList<PluginInfo>();
                Matrix mLatency = generateComparisonMatrix(pluginInfos, "latency");
                Matrix mUptime = generateComparisonMatrix(pluginInfos, "uptime");
                Matrix mOccupied = generateComparisonMatrix(pluginInfos, "occupied");
                Matrix mCores = generateComparisonMatrix(pluginInfos, "cores");
                List<Double> prioritiesLatency = getPrioritiesOnMatrix(mLatency);
                List<Double> prioritiesUptime = getPrioritiesOnMatrix(mUptime);
                List<Double> prioritiesOccupied = getPrioritiesOnMatrix(mOccupied);
                List<Double> prioritiesCores = getPrioritiesOnMatrix(mCores);
                List<Double> priorities1 = multiplyVectors(prioritiesLatency, prioritiesUptime);
                List<Double> priorities2 = multiplyVectors(priorities1, prioritiesOccupied);
                List<Double> priorities3 = multiplyVectors(priorities2, prioritiesCores);
                List<Double> priorities = priorities3;

                // DEBUG
                for (int i = 0; i < priorities.size(); ++i) {
                        System.out.println(prioritiesLatency.get(i) + " " + prioritiesUptime.get(i) + " " + priorities.get(i));
                }
                
                while (!priorities.isEmpty()) {
                        int index = getMaxNumberIndex(priorities);
                        plugins.add(pluginInfos.get(index));
                        pluginInfos.remove(index);
                        priorities.remove(index);
                }
                return plugins;
        }
        
        public static PluginInfo getBestService(List<PluginInfo> pluginInfos) throws SchedException {
                if (pluginInfos.isEmpty()) return null;
                return getServiceOrderedByPriority(pluginInfos).get(pluginInfos.size());
        }
        
        private List<PluginInfo> filterByService(long serviceId, List<PluginInfo> plgs) throws SchedException {
                ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
                for (PluginInfo pluginInfo : plgs) {
                        if (pluginInfo.getService(serviceId) != null)
                                plugins.add(pluginInfo);
                }
                
                if (plugins.size() == 0) {
                        throw new SchedException("Service not available.");
                }
                
                return plugins;
        }
        
        private List<PluginInfo> filterByUsed() throws SchedException {
        	ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
        	for (PluginInfo pluginInfo : getCloudMap().values()) {
                if (!usedResources.contains(pluginInfo))
                        plugins.add(pluginInfo);
        	}
        
        	if (plugins.size() == 0) {
                return new ArrayList<PluginInfo>(getCloudMap().values());
        	}
        
        	return plugins;
        }
        
        public Matrix inducedMatrix(Matrix matrix, double n) {
                return matrix.arrayTimes(matrix).minus(matrix.times(n));
        }
}

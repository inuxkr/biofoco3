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
                List<PluginInfo> plugins = filterByService(jobInfo.getServiceId());
                return getBestService(plugins);
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
                List<Double> prioritiesLatency = getPrioritiesOnMatrix(mLatency);
                List<Double> prioritiesUptime = getPrioritiesOnMatrix(mUptime);
                List<Double> priorities = multiplyVectors(prioritiesLatency, prioritiesUptime);
                
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unb.cic.bionimbus.services.sched.policy.impl;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;
import br.unb.cic.bionimbus.services.ZooKeeperService;
import br.unb.cic.bionimbus.services.sched.policy.SchedPolicy;
import br.unb.cic.bionimbus.utils.Pair;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.twitter.common.zookeeper.ZooKeeperClient;
import org.apache.zookeeper.KeeperException;
import org.codehaus.jackson.map.ObjectMapper;

public class RRPolicy extends SchedPolicy {

    private List<PluginInfo> listPlugin;
    private Map<PluginTask, Integer> blackList = new HashMap<PluginTask, Integer>();
    private static final int BLACKLIST_LIMIT = 12;
    private static final String SCHED = "/sched";
    private ZooKeeperService zk;

    @Override
    public HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos, ZooKeeperService zk) {
        this.zk = zk;
        //condição para verificar se a chamada foi apenas para iniciar o zk
        if (jobInfos == null) {
            return null;
        }

        HashMap jobCloud = new HashMap<JobInfo, PluginInfo>();
        JobInfo job = null;
        if (jobInfos.size() > 0) 
        	job = jobInfos.iterator().next();
        
        // escalonador irá receber um zookeeperService como parâmetro
        jobCloud.put(job, scheduleJob(job));

        return jobCloud;

    }

    /**
     * Recebe um job a ser escalonado, envia o job para a execução do algoritmo
     * ACO onde os recursos irão ser classificados e retornados para que o
     * melhor entre eles seja retornado.
     *
     * @param jobInfo contém as informações do job que deve ser escalonado
     * @return o melhor recurso disponível para executar o job informado
     */
    private PluginInfo scheduleJob(JobInfo jobInfo) {
        listPlugin = getExactClouds(jobInfo);

        int numero = (int) (Math.random() * listPlugin.size());
        PluginInfo plugin = listPlugin.get(numero);

        return plugin. getId() == null ? null : plugin;
    }

    /**
     * Retorna as melhores nuvens para realizar o serviço, nuvens que rodam o
     * tipo de serviço requerido e se devem ser privadas ou publicas.
     *
     * @param jobInfo
     * @return lista com as nuvens que rodam o serviço requrido
     */
    public List getExactClouds(JobInfo jobInfo) {
        //seleciona as nuvens disponíveis para o tipo informado
        List cloudList = filterTypeCloud(getCloudMap().values(), 2);

        //configurar o tipo de serviço requerido
        filterByService(jobInfo.getServiceId(), cloudList);

        return cloudList;

    }

    @Override
    public synchronized List<PluginTask> relocate(Collection<Pair<JobInfo, PluginTask>> taskPairs) {
        List<PluginTask> tasksToCancel = new ArrayList<PluginTask>();
        for (Pair<JobInfo, PluginTask> taskPair : taskPairs) {
            PluginTask task = taskPair.getSecond();
            JobInfo job = taskPair.getFirst();

            if (PluginTaskState.RUNNING.equals(task.getState())) {
                if (blackList.containsKey(task)) {
                    blackList.remove(task);
                }
            }

            if (!PluginTaskState.WAITING.equals(task.getState())) {
                continue;
            }

            int count = 0;
            if (blackList.containsKey(task)) {
                count = blackList.get(task);
            }

            blackList.put(task, count + 1);

            if (blackList.get(task) >= BLACKLIST_LIMIT) {
                if (job != null) {
                    tasksToCancel.add(task);
                }
            }
        }

        for (PluginTask task : tasksToCancel) {
            blackList.remove(task);
        }

        return tasksToCancel;
    }

    @Override
    public void cancelJobEvent(PluginTask task) {
    }

    @Override
    public void jobDone(PluginTask task) {
    }

    /**
     * Retorna o tamanho total dos arquivos de entrada de um job.
     *
     * @param jobInfo
     * @return
     */
    private static Long getTotalSizeOfJobsFiles(JobInfo jobInfo) {
        long sum = 0;

        for (Pair<String, Long> pair : jobInfo.getInputs()) {
            sum += pair.second;
        }

        return sum;
    }

    /**
     * Retorna o nome do maior arquivo de entrda do job.
     *
     * @param jobInfos
     * @return
     */
    public static String getBiggerInputJob(JobInfo jobInfo) {
        Pair<String, Long> file = null;
        for (Pair<String, Long> pair : jobInfo.getInputs()) {
            if (file == null || file.second < pair.second) {
                file = pair;
            }
        }

        return file.first;
    }

    /**
     * Seleciona o tipo de nuvem para escalonar, pública, privada ou ambas. 0 -
     * pública 1 - privada 2 - hibrida
     */
    public List<PluginInfo> filterTypeCloud(Collection<PluginInfo> plugins, int type) {
        if (type == 2) {
            return new ArrayList<PluginInfo>(plugins);
        }

        List<PluginInfo> clouds = new ArrayList<PluginInfo>();
        for (PluginInfo pluginInfo : plugins) {
            //COndição para verificar se nuvem é do tipo solicitada, privada(1), pública(0) ou hibrida(2),
            if (pluginInfo.getPrivateCloud() == type) {
                clouds.add(pluginInfo);
            }
        }
        return clouds;
    }

    /**
     * Seleciona apenas as nuvens informadas na Collection
     *
     * @param plgs e que tem o serviço informado no
     * @param serviceId.
     *
     * @param plgs
     * @param serviceId
     */
    private void filterByService(long serviceId, Collection<PluginInfo> plgs) {
        ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();

        for (PluginInfo pluginInfo : plgs) {
            if (pluginInfo.getService(serviceId) != null) {
                plugins.add(pluginInfo);
            }
        }
        plgs.retainAll(plugins);

    }

    /**
     * Recupera os dados do
     *
     * @param plugin, pluginInfo, armazenados no zookeeper de acordo com o
     * @param dir, diretorio, informado.
     * @param plugin identificação do recurso que deve ser retirado os dados.
     * @param dir diretório do zookeeper que contém as informações desejadas.
     * @return dados contidos no diretorio
     */
    private String getDatasZookeeper(String zkPath, String dir) {
        String datas = "";
        try {
            if (zk.getZNodeExist(zkPath + dir, false)) {
                datas = zk.getData(zkPath + dir, null);
            }
        } catch (KeeperException ex) {
            Logger.getLogger(RRPolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RRPolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return datas;
    }

    /**
     * Recupera os dados do
     *
     * @param plugin, pluginInfo, armazenados no zookeeper de acordo com o
     * @param dir, diretorio, informado.
     * @param plugin identificação do recurso que deve ser retirado os dados.
     * @param dir diretório do zookeeper que contém as informações desejadas.
     * @return dados contidos no diretorio
     */
    private void setDatasZookeeper(String zkPath, String dir, String datas) {
        try {
            if (zk.getZNodeExist(zkPath + dir, false)) {
                zk.setData(zkPath + dir, datas);
            }
        } catch (KeeperException ex) {
            Logger.getLogger(RRPolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RRPolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ZooKeeperClient.ZooKeeperConnectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public HashMap<JobInfo, PluginInfo> schedule(Collection<JobInfo> jobInfos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPolicyName() {
        return "Name: " + RRPolicy.class.getSimpleName() + " - Número: 0";
    }
}
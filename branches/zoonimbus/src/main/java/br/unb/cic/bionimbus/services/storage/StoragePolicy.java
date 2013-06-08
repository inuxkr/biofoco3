/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unb.cic.bionimbus.services.storage;


import br.unb.cic.bionimbus.plugin.PluginInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author deric
 */
public class StoragePolicy {

    private double peso_latency = 0.5;
    private double peso_space = 0.2;
    private double peso_uptime = 0.3;

    Collection<PluginInfo> best = new ArrayList<PluginInfo>();

    private final Map<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

    /*
     * Calcular o custo de armazenamento de uma nuvem //ta passando so 1 plugin
     */
    public long calcBestCost(PluginInfo pluginInfo) {

        long cost;
        long uptime;

        /*Calculando os custos de armazenamento dos peers
         *Custo = (Espaço livre + Uptime) * Latencia
         */
        uptime = pluginInfo.getUptime() / 1000;
        cost = (long) (((pluginInfo.getFsFreeSize() * peso_space) + 
                (uptime * peso_uptime)) * 
                (pluginInfo.getLatency() * peso_latency));

        return cost;

    }

    /**
     * Quicksort para ordenar as melhores nuvens para armazenar os dados de acordo com o 
     * custo de armazenamento das nuvens
     */
    public List<PluginInfo> SortPlugins(List<PluginInfo> plugins){  
        
        /*
         * Metodo Sort para ordenar os plugins de acordo com o custo de armazenamento
         */
        Collections.sort(plugins,new Comparator<PluginInfo>() {

            
            @Override
            public int compare(PluginInfo o1, PluginInfo o2) {
                return Long.compare(o1.getStorageCost(),o2.getStorageCost());    
            }
        });

        return plugins;
    } 
        
    /*
     * Método para receber a map com os plugins e converter em List, para ficar mais fácil o tratamento dos dados
     */
    public List<PluginInfo> SwapTypePlugin(Collection<PluginInfo> plugins){
        List<PluginInfo> plugin = new ArrayList<PluginInfo>();

        for (PluginInfo pluginInfo : plugins) {
            plugin.add(pluginInfo);
        }
        return plugin;
    } 
}
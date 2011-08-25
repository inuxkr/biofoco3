package br.unb.cic.bionimbus.sched;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedReqMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginService;

public class SchedService implements Service, P2PListener, Runnable {

	private final int CORES_WEIGHT = 3;
	private final int NODES_WEIGHT = 2;
	
	
	private final ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ScheduledExecutorService schedExecService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("SchedService-%d").build());

	private P2PService p2p = null;

	public SchedService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running SchedService...");
		Message msg = new CloudReqMessage();
		p2p.sendMessage(msg);
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
	}

	@Override
	public void shutdown() {
		p2p.remove(this);
		schedExecService.shutdownNow();
	}

	@Override
	public void getStatus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(P2PEvent event) {
		if (event.getType() != P2PEventType.MESSAGE)
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;

		switch (P2PMessageType.values()[msg.getType()]) {
		case CLOUDRESP:
			CloudRespMessage cloudMsg = (CloudRespMessage) msg;
			for (PluginInfo info : cloudMsg.values()) {
				System.out.println("plugin = " + info.getId());
				System.out.println("fsSize = " + info.getFsSize());
				System.out.println("fsFreeSize = " + info.getFsFreeSize());
				System.out.println("numNodes = " + info.getNumNodes());
				System.out.println("numCores = " + info.getNumCores());
				for (PluginService serv : info.getServices()) {
					System.out.println("\tservice = " + serv.getName());
				}
				System.out.println();

				cloudMap.put(info.getId(), info);
			}
			break;
		case SCHEDREQ:
			SchedReqMessage schedMsg = (SchedReqMessage) msg;
			scheduleJob(schedMsg.getJobInfo());
			break;
		}
	}

	private void scheduleJob(JobInfo jobInfo) {
		try {
			List<PluginInfo> availablePlugins = filterByService(jobInfo.getServiceId());
			PluginInfo pluginInfo = getBestPluginForJob(availablePlugins, jobInfo);
			SchedRespMessage msg = new SchedRespMessage();
			msg.setJobId(jobInfo.getId());
			msg.setPluginId(pluginInfo.getId());
			p2p.sendMessage(msg);		
		} catch (SchedException ex) {
			Message errMsg = new SchedErrorMessage(jobInfo.getId(), ex.getMessage());
			p2p.sendMessage(errMsg);	
		}
	}
	
	private List<PluginInfo> filterByService(long serviceId) throws SchedException {
		ArrayList<PluginInfo> plugins = new ArrayList<PluginInfo>();
		for (PluginInfo pluginInfo : cloudMap.values()) {
			if (pluginInfo.getService(serviceId) != null)
				plugins.add(pluginInfo);
		}
		
		if (plugins.size() == 0) {
			throw new SchedException("Service not found.");
		}
		
		return plugins;
	}
	
	private PluginInfo getBestPluginForJob(List<PluginInfo> plugins, JobInfo job) throws SchedException {
		// O que fazer para pegar o tamanho do job?
		// Como fazer para pegar a lista de jobs em execucao no momento? E a de jobs pendentes?
		
		if (plugins.size() == 0) {
			throw new SchedException("Empty plugins list sent to sched alghorithm.");
		}
		
		PluginInfo best = plugins.get(0);
		for (PluginInfo plugin : plugins) {
			if (calculateWeightSum(plugin) > calculateWeightSum(best)) best = plugin;
		}
		
		return best;
	}
	
	private int calculateWeightSum(PluginInfo plugin) {
		return (plugin.getNumCores() * CORES_WEIGHT) + (plugin.getNumNodes() * NODES_WEIGHT);
	}
}

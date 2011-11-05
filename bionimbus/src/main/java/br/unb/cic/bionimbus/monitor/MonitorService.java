package br.unb.cic.bionimbus.monitor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import br.unb.cic.bionimbus.Service;
import br.unb.cic.bionimbus.ServiceManager;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.AbstractMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobRespMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedReqMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.sched.SchedException;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;
import br.unb.cic.bionimbus.utils.Pair;

public class MonitorService implements Service, P2PListener, Runnable {

	private SchedPolicy schedPolicy;
	
	private final ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();
	
	private final ScheduledExecutorService schedExecService = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder().namingPattern("MonitorService-%d").build());
	
	private final Map<String, JobInfo> pendingJobs = new ConcurrentHashMap<String, JobInfo>();
	
	private final Map<String, Pair<JobInfo, PluginTask>> runningJobs = new ConcurrentHashMap<String, Pair<JobInfo, PluginTask>>();

	private P2PService p2p = null;

	public MonitorService(ServiceManager manager) {
		manager.register(this);
	}

	public SchedPolicy getPolicy() {
		if (schedPolicy == null) {
			schedPolicy = SchedPolicy.getInstance(cloudMap);
		}
		return schedPolicy;
	}
	
	@Override
	public void run() {
		System.out.println("running MonitorService...");
		
		checkPendingJobs();
		checkRunningJobs();
		Message msg = new CloudReqMessage(p2p.getPeerNode());
		p2p.broadcast(msg); //TODO: isto é broadcast?
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
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
	
	private void checkPendingJobs() {
		// TODO aqui temos que checar os jobs que estão aguardando o escalonamento.
		// precisamos esperar um timeout ate' fazer nova requisicao.
	}
	
	private void checkRunningJobs() {
		PeerNode peer = p2p.getPeerNode();
		for (String taskId : runningJobs.keySet()) {
			sendStatusReq(peer, taskId);
		}
	}

	@Override
	public void onEvent(P2PEvent event) {
		if (!event.getType().equals(P2PEventType.MESSAGE))
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;
		
		PeerNode sender = p2p.getPeerNode();
		PeerNode receiver = null;
		
		if (msg instanceof AbstractMessage) {
			receiver = ((AbstractMessage) msg).getPeer();
		}

		switch (P2PMessageType.of(msg.getType())) {
		case JOBREQ:
			JobReqMessage jobMsg = (JobReqMessage) msg;
			sendSchedReq(sender, jobMsg.getJobInfo());
			break;
		case SCHEDRESP:
			SchedRespMessage schedMsg = (SchedRespMessage) msg;
			JobInfo schedJob = pendingJobs.get(schedMsg.getJobId());
			sendStartReq(sender, schedMsg.getPluginInfo().getHost(), schedJob);
			break;
		case STARTRESP:
			StartRespMessage respMsg = (StartRespMessage) msg;
			sendJobResp(sender, receiver, respMsg.getJobId(), respMsg.getPluginTask());
			break;
		case STATUSRESP:
			StatusRespMessage status = (StatusRespMessage) msg;
			updateJobStatus(status.getPluginTask());
			break;
		case CLOUDRESP:
			CloudRespMessage cloudMsg = (CloudRespMessage) msg;
			for (PluginInfo info : cloudMsg.values()) 
				cloudMap.put(info.getId(), info);
			break;
		case SCHEDREQ:
			SchedReqMessage schedReqMsg = (SchedReqMessage) msg;
			scheduleJob(sender, receiver, schedReqMsg.getJobInfo());
			break;
		case END:
			EndMessage end = (EndMessage) msg;
			finalizeJob(end.getTask());
			break;
		case ERROR:
			ErrorMessage errMsg = (ErrorMessage) msg;
			System.out.println("SCHED ERROR: type="
					+ errMsg.getErrorType().toString() + ";msg="
					+ errMsg.getError());
			break;
		}
	}
	
	private void scheduleJob(PeerNode sender, PeerNode receiver, JobInfo jobInfo) {
		try {
			PluginInfo pluginInfo = getPolicy().schedule(jobInfo);
			SchedRespMessage msg = new SchedRespMessage(sender);
			msg.setJobId(jobInfo.getId());
			msg.setPluginInfo(pluginInfo);
			p2p.sendMessage(receiver.getHost(), msg);		
		} catch (SchedException ex) {
			Message errMsg = new SchedErrorMessage(sender, jobInfo.getId(), ex.getMessage());
			p2p.sendMessage(receiver.getHost(), errMsg);	
		}
	}

	private void sendSchedReq(PeerNode sender, JobInfo jobInfo) {
		jobInfo.setId(UUID.randomUUID().toString());
		pendingJobs.put(jobInfo.getId(), jobInfo);
		SchedReqMessage newMsg = new SchedReqMessage(sender, jobInfo);
		p2p.broadcast(newMsg);
	}
	
	private void sendJobResp(PeerNode sender, PeerNode receiver, String jobId, PluginTask task) {
		JobInfo jobInfo = pendingJobs.remove(jobId);
		runningJobs.put(task.getId(), new Pair<JobInfo, PluginTask>(jobInfo, task));
		JobRespMessage jobRespMsg = new JobRespMessage(sender, jobInfo);
		p2p.broadcast(jobRespMsg); // mandar direto pro cliente
	}
	
	private void sendStartReq(PeerNode sender, Host dest, JobInfo jobInfo) {
		StartReqMessage startMsg = new StartReqMessage(sender, jobInfo);
		p2p.sendMessage(dest, startMsg);
	}
	
	private void sendStatusReq(PeerNode sender, String taskId) {
		StatusReqMessage msg = new StatusReqMessage(sender, taskId);
		p2p.broadcast(msg); //TODO: isto é realmente um broadcast?
	}
	
	private void updateJobStatus(PluginTask task) {
		Pair<JobInfo, PluginTask> pair = runningJobs.get(task.getId());
		JobInfo job = pair.first;
		runningJobs.put(task.getId(), new Pair<JobInfo, PluginTask>(job, task));
	}
	
	private void finalizeJob(PluginTask task) {
		Pair<JobInfo, PluginTask> pair = runningJobs.remove(task.getId());
		JobInfo job = pair.first;
		//p2p.sendMessage(new EndJobMessage(job));
	}

}

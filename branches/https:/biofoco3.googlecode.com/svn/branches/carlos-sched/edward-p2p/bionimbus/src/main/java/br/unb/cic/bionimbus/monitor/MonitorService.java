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
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.AbstractMessage;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobRespMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedReqMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.utils.Pair;

public class MonitorService implements Service, P2PListener, Runnable {

	private final ScheduledExecutorService schedExecService = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder().namingPattern("MonitorService-%d").build());
	
	private final Map<String, JobInfo> pendingJobs = new ConcurrentHashMap<String, JobInfo>();
	
	private final Map<String, Pair<JobInfo, PluginTask>> runningJobs = new ConcurrentHashMap<String, Pair<JobInfo, PluginTask>>();

	private P2PService p2p = null;

	public MonitorService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running MonitorService...");
		
		checkPendingJobs();
		checkRunningJobs();
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 2, TimeUnit.MINUTES);
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
		if (event.getType() != P2PEventType.MESSAGE)
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

		switch (P2PMessageType.values()[msg.getType()]) {
		case JOBREQ:
			JobReqMessage jobMsg = (JobReqMessage) msg;
			sendSchedReq(sender, receiver, jobMsg.getJobInfo());
			break;
		case SCHEDRESP:
			SchedRespMessage schedMsg = (SchedRespMessage) msg;
			JobInfo schedJob = pendingJobs.get(schedMsg.getJobId());
			sendStartReq(sender, receiver, schedJob);
			break;
		case STARTRESP:
			StartRespMessage respMsg = (StartRespMessage) msg;
			sendJobResp(sender, receiver, respMsg.getJobId(), respMsg.getPluginTask());
			break;
		case STATUSRESP:
			StatusRespMessage status = (StatusRespMessage) msg;
			updateJobStatus(status.getPluginTask());
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
	
	private void sendSchedReq(PeerNode sender, PeerNode receiver, JobInfo jobInfo) {
		jobInfo.setId(UUID.randomUUID().toString());
		pendingJobs.put(jobInfo.getId(), jobInfo);
		SchedReqMessage newMsg = new SchedReqMessage(sender, jobInfo);
		p2p.sendMessage(receiver.getHost(), newMsg);
	}
	
	private void sendJobResp(PeerNode sender, PeerNode receiver, String jobId, PluginTask task) {
		JobInfo jobInfo = pendingJobs.remove(jobId);
		runningJobs.put(task.getId(), new Pair<JobInfo, PluginTask>(jobInfo, task));
		JobRespMessage jobRespMsg = new JobRespMessage(sender, jobInfo);
		p2p.sendMessage(receiver.getHost(), jobRespMsg);
	}
	
	private void sendStartReq(PeerNode sender, PeerNode receiver, JobInfo jobInfo) {
		StartReqMessage startMsg = new StartReqMessage(sender, jobInfo);
		p2p.sendMessage(receiver.getHost(), startMsg);
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
		job.setOutputs(null);
		//p2p.sendMessage(new EndJobMessage(job));
	}

}

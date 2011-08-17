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
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobRespMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedReqMessage;
import br.unb.cic.bionimbus.p2p.messages.SchedRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;

public class MonitorService implements Service, P2PListener, Runnable {

	private final ScheduledExecutorService schedExecService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("MonitorService-%d").build());
	
	private final Map<String, JobInfo> newJobs = new ConcurrentHashMap<String, JobInfo>();
	
	private final Map<String, JobInfo> runJobs = new ConcurrentHashMap<String, JobInfo>();

	private P2PService p2p = null;

	public MonitorService(ServiceManager manager) {
		manager.register(this);
	}

	@Override
	public void run() {
		System.out.println("running MonitorService...");
		
		// checar jobs ainda nao escalonados ou precisando reescalonar
		// e checar status dos jobs ja' iniciados
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

	@Override
	public void onEvent(P2PEvent event) {
		if (event.getType() != P2PEventType.MESSAGE)
			return;

		P2PMessageEvent msgEvent = (P2PMessageEvent) event;
		Message msg = msgEvent.getMessage();
		if (msg == null)
			return;

		// recebe eventos de endTask()

		switch (P2PMessageType.values()[msg.getType()]) {
		case JOBREQ:
			JobReqMessage jobMsg = (JobReqMessage) msg;
			sendSchedReq(jobMsg.getJobInfo());
			break;
		case SCHEDRESP:
			SchedRespMessage schedMsg = (SchedRespMessage) msg;
			JobInfo schedJob = newJobs.get(schedMsg.getJobId());
			sendStartReq(schedJob);
			break;
		case STARTRESP:
			StartRespMessage respMsg = (StartRespMessage) msg;
			JobInfo startJob = respMsg.getJobInfo();
			sendJobResp(startJob);
			break;
		case ERROR:
			ErrorMessage errMsg = (ErrorMessage) msg;
			System.out.println("SCHED ERROR: type="
					+ errMsg.getErrorType().toString() + ";msg="
					+ errMsg.getError());
			break;
		}
	}
	
	private void sendSchedReq(JobInfo jobInfo) {
		jobInfo.setId(UUID.randomUUID().toString());
		newJobs.put(jobInfo.getId(), jobInfo);
		SchedReqMessage newMsg = new SchedReqMessage(jobInfo);
		p2p.sendMessage(newMsg);
	}
	
	private void sendJobResp(JobInfo jobInfo) {
		newJobs.remove(jobInfo.getId());
		runJobs.put(jobInfo.getId(), jobInfo);
		JobRespMessage jobRespMsg = new JobRespMessage(jobInfo);
		p2p.sendMessage(jobRespMsg);
	}
	
	private void sendStartReq(JobInfo jobInfo) {
		StartReqMessage startMsg = new StartReqMessage(jobInfo);
		p2p.sendMessage(startMsg);
	}

}

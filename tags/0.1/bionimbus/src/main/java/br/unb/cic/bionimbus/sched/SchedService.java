package br.unb.cic.bionimbus.sched;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import br.unb.cic.bionimbus.p2p.messages.CancelReqMessage;
import br.unb.cic.bionimbus.p2p.messages.CancelRespMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.CloudRespMessage;
import br.unb.cic.bionimbus.p2p.messages.EndMessage;
import br.unb.cic.bionimbus.p2p.messages.ErrorMessage;
import br.unb.cic.bionimbus.p2p.messages.JobCancelReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobCancelRespMessage;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobRespMessage;
import br.unb.cic.bionimbus.p2p.messages.ListReqMessage;
import br.unb.cic.bionimbus.p2p.messages.ListRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StartReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StartRespMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusReqMessage;
import br.unb.cic.bionimbus.p2p.messages.StatusRespMessage;
import br.unb.cic.bionimbus.plugin.PluginFile;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;
import br.unb.cic.bionimbus.plugin.PluginTaskState;
import br.unb.cic.bionimbus.sched.policy.SchedPolicy;
import br.unb.cic.bionimbus.utils.Pair;

public class SchedService implements Service, P2PListener, Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchedService.class);
	
	private final ConcurrentHashMap<String, PluginInfo> cloudMap = new ConcurrentHashMap<String, PluginInfo>();

	private final ScheduledExecutorService schedExecService = Executors
			.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
					.namingPattern("SchedService-%d").build());
	
//	private final ExecutorService executorService = Executors
//			.newCachedThreadPool(new BasicThreadFactory.Builder()
//					.namingPattern("SchedService-sched-%d").build());

	private final Map<String, JobInfo> pendingJobs = new ConcurrentHashMap<String, JobInfo>();
	
	private final Map<String, Pair<JobInfo, PluginTask>> runningJobs = new ConcurrentHashMap<String, Pair<JobInfo, PluginTask>>();
	
	private final Map<String, Pair<String, Host>> cancelingJobs = new ConcurrentHashMap<String, Pair<String, Host>>();
	
	private P2PService p2p = null;

	private SchedPolicy schedPolicy;

	public SchedService(ServiceManager manager) {
		manager.register(this);
	}
	
	public SchedPolicy getPolicy() {
		if (schedPolicy == null) {
			schedPolicy = SchedPolicy.getInstance(cloudMap);
		}
		schedPolicy.setCloudMap(cloudMap);
		return schedPolicy;
	}

	@Override
	public void run() {
		LOG.debug("running SchedService...");
		
		checkPendingJobs();
		checkRunningJobs();
		Message msg = new CloudReqMessage(p2p.getPeerNode());
		p2p.broadcast(msg);
	}

	@Override
	public void start(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
		schedExecService.scheduleAtFixedRate(this, 0, 15, TimeUnit.SECONDS);
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
		case CLOUDRESP:
			CloudRespMessage cloudMsg = (CloudRespMessage) msg;
			for (PluginInfo info : cloudMsg.values()) 
				cloudMap.put(info.getId(), info);
			break;
		case JOBREQ:
			JobReqMessage jobMsg = (JobReqMessage) msg;
			for (JobInfo jobInfo : jobMsg.values()) {
				jobInfo.setId(UUID.randomUUID().toString());
				jobInfo.setTimestamp(System.currentTimeMillis());
				LOG.debug("Wild job " + jobInfo.getId() + " appears.");
				pendingJobs.put(jobInfo.getId(), jobInfo);
			}
			updateJobsFileSize(sender);
			break;
		case STARTRESP:
			StartRespMessage respMsg = (StartRespMessage) msg;
			sendJobResp(sender, receiver, respMsg.getJobId(), respMsg.getPluginTask());
			break;
		case STATUSRESP:
			StatusRespMessage status = (StatusRespMessage) msg;
			updateJobStatus(status.getPluginTask());
			break;
		case JOBCANCELREQ:
			JobCancelReqMessage cancel = (JobCancelReqMessage) msg;
			cancelJob(cancel.getPeerNode().getHost(), cancel.getJobId());
			break;
		case CANCELRESP:
			CancelRespMessage cancelResp = (CancelRespMessage) msg;
			finalizeJob(cancelResp.getPluginTask());
			Pair<String, Host> pair = cancelingJobs.get(cancelResp.getPluginTask().getId());
			p2p.sendMessage(pair.second, new JobCancelRespMessage(p2p.getPeerNode(), pair.first));
			break;
		case LISTRESP:
			ListRespMessage listResp = (ListRespMessage) msg;
			fillJobFileSize(listResp.values());
			scheduleJobs(sender, receiver);
			break;
		case END:
			EndMessage end = (EndMessage) msg;
			finalizeJob(end.getTask());
			break;
		case ERROR:
			ErrorMessage errMsg = (ErrorMessage) msg;
			LOG.warn("SCHED ERROR: type="
					+ errMsg.getErrorType().toString() + ";msg="
					+ errMsg.getError());
			break;
		}
	}
	
	private void fillJobFileSize(Collection<PluginFile> pluginFiles) {
		for (JobInfo job : pendingJobs.values()) {
			List<Pair<String, Long>> pairList = new ArrayList(job.getInputs());
			for (Pair<String, Long> pair : pairList) {
				String fileId = pair.first;
				PluginFile file = getFileById(fileId, pluginFiles);

				if (file != null) {
					job.addInput(file.getId(), file.getSize());
				} else {
					LOG.debug("File returned null.");
				}
			}
		}
	}
	
	private PluginFile getFileById(String fileId, Collection<PluginFile> pluginFiles) {
		for (PluginFile file : pluginFiles) {
			if (file.getId().equals(fileId)) {
				return file;
			}
		}
		return null;
	}
	
	private void sendStartReq(PeerNode sender, Host dest, JobInfo jobInfo) {
		StartReqMessage startMsg = new StartReqMessage(sender, jobInfo);
		p2p.sendMessage(dest, startMsg);
	}
	
	private void sendStatusReq(PeerNode sender, String taskId) {
		StatusReqMessage msg = new StatusReqMessage(sender, taskId);
		p2p.broadcast(msg); //TODO: isto é realmente um broadcast?
	}
	
	private JobInfo getJobInfoFromPair(Pair<JobInfo, PluginTask> pair) {
		JobInfo ret = null;
				
		if (pair.second != null) {
			ret = pair.second.getJobInfo();
		}
			
		if (pair.first != null) {
			ret = pair.first;
		}
		
		return ret;
	}
	
	private void updateJobStatus(PluginTask task) {
		Pair<JobInfo, PluginTask> pair = runningJobs.get(task.getId());
		JobInfo job = pair.first;
		
		//if (task.getState().equals(PluginTaskState.WAITING)) {
		//	runningJobs.remove(task.getId());
		//	cancelJob(p2p.getPeerNode().getHost(), getJobInfoFromPair(pair).getId());
		//	pendingJobs.put(getJobInfoFromPair(pair).getId(), getJobInfoFromPair(pair));
		//} else {
			runningJobs.put(task.getId(), new Pair<JobInfo, PluginTask>(job, task));
		//}
	}
	
	private void updateJobsFileSize(PeerNode sender) {
		ListReqMessage listReqMsg = new ListReqMessage(sender);
		p2p.broadcast(listReqMsg);
	}
	
	private void sendJobResp(PeerNode sender, PeerNode receiver, String jobId, PluginTask task) {
		if (task == null) {
			LOG.debug("Job " + jobId + " não possui serviço rodando.");
			JobRespMessage jobRespMsg = new JobRespMessage(sender, null);
			p2p.broadcast(jobRespMsg);
		} else {
			LOG.info("Job " + jobId + " movido para a lista de jobs rodando.");
			JobInfo jobInfo = pendingJobs.remove(jobId);
			runningJobs.put(task.getId(), new Pair<JobInfo, PluginTask>(jobInfo, task));
			JobRespMessage jobRespMsg = new JobRespMessage(sender, jobInfo);
			p2p.broadcast(jobRespMsg);
			scheduleJobs(sender, receiver);
		}
	}
	
	private void finalizeJob(PluginTask task) {
		Pair<JobInfo, PluginTask> pair = runningJobs.remove(task.getId());
		
		if (pair != null) {
			JobInfo job = getJobInfoFromPair(pair);
		
			LOG.info("Job " + job.getId() + " executado em " + ((float)(System.currentTimeMillis() - job.getTimestamp()) / 1000) + " segundos");
			//p2p.sendMessage(new EndJobMessage(job));
		}
	}
	
	private void scheduleJobs(PeerNode sender, PeerNode receiver) {
		if (pendingJobs.size() == 0) return;
		LOG.info("--- Inicio de escalonamento ---");
		HashMap<JobInfo, PluginInfo> schedMap = getPolicy().schedule(pendingJobs.values());
		for (Map.Entry<JobInfo,PluginInfo> entry : schedMap.entrySet()) {
			JobInfo jobInfo = entry.getKey();
			PluginInfo pluginInfo = entry.getValue();
			
			if (pluginInfo == null) {
				sendJobResp(sender, receiver, jobInfo.getId(), null);
			} else {
				LOG.info(jobInfo.getId() + " scheduled to " + pluginInfo.getId());
				sendStartReq(sender, pluginInfo.getHost(), jobInfo);
			}
		}
		LOG.info("--- Fim de escalonamento ---");
			
	}
	
	private void cancelJob(Host origin, String jobId) {
		if (pendingJobs.containsKey(jobId)) {
			pendingJobs.remove(jobId);
			return;
		}

		for (Pair<JobInfo, PluginTask> pair : runningJobs.values()) {
			if (pair.first.getId().equals(jobId)) {
				cancelingJobs.put(pair.second.getId(), new Pair<String, Host>(jobId, origin));
				CancelReqMessage msg = new CancelReqMessage(p2p.getPeerNode(), pair.second.getId());
				p2p.broadcast(msg);
				return;
			}
		}
		
	}
}
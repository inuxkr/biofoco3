package br.unb.cic.bionimbus.p2p.messages;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;

import br.unb.cic.bionimbus.client.FileInfo;
import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.plugin.PluginInfo;
import br.unb.cic.bionimbus.plugin.PluginTask;

public class BulkMessage {

	private String peerID;
	private Host host;
	
	@JsonIgnore
	private Collection<PluginInfo> values;
	
	@JsonIgnore
	private PluginTask task;
	
	@JsonIgnore
	private PluginInfo pluginInfo;
	
	@JsonIgnore
	private JobInfo jobInfo;
	
	@JsonIgnore
	private FileInfo fileInfo;
	
	@JsonIgnore
	private String jobId;
	
	@JsonIgnore
	private String pluginId;
	
	@JsonIgnore
	private String taskId;

	@JsonIgnore
	private String error;
			
	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
	
	public String getPeerID() {
		return peerID;
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

	public void setValues(Collection<PluginInfo> values) {
		this.values = values;
	}

	public Collection<PluginInfo> getValues() {
		return values;
	}

	public void setTask(PluginTask task) {
		this.task = task;
	}

	public PluginTask getTask() {
		return task;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public void setJobInfo(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}

	public JobInfo getJobInfo() {
		return jobInfo;
	}
	
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}	
	
}

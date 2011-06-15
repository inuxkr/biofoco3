package br.biofoco.cloud.services;

public class TaskResult {
	
	private long taskID;
	
	private String result;

	public void setResult(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public long getTaskID() {
		return taskID;
	}

	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}
	

}

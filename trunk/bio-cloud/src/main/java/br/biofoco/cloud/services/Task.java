package br.biofoco.cloud.services;

import java.util.List;

public class Task {
	
	enum State {
		PENDING,
		RUNNING,
		ABORTED,
		COMPLETED;
	}
	
	private long taskId;
	
	private long serviceId;
	
	private State state;
	
	private List<String> args;

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long id) {
		this.taskId = id;
	}

	public long getServiceId() {
		return serviceId;
	}

	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isRunning() {	
		return state == State.RUNNING;
	}

	public boolean isCompleted() {
		return state == State.COMPLETED;
	}

	public boolean isAborted() {
		return state == State.ABORTED;
	}

	public TaskResult execute() throws Exception {
		return null;
	}
}

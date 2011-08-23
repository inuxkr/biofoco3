package br.unb.cic.bionimbus.client;

import java.util.List;
import java.util.UUID;

public class JobInfo {
	
	private String id = UUID.randomUUID().toString();
	private long serviceId;
	private List<String> args;
	private List<String> inputs;
	private List<String> outputs;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public List<String> getInputs() {
		return inputs;
	}
	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}
	public List<String> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
}

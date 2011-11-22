package br.unb.cic.bionimbus.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobInfo {

	private String id = UUID.randomUUID().toString();

	private long serviceId;

	private String args = "";
	
	private long fileSize;

	private Map<String, Long> inputs = new HashMap<String, Long>();

	private List<String> outputs = new ArrayList<String>();

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

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public Map<String, Long> getInputs() {
		return inputs;
	}

	public void addInput(String id, Long size) {
		inputs.put(id, size);
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void addOutput(String name) {
		outputs.add(name);
	}
	
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}

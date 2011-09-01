package br.unb.cic.bionimbus.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JobInfo {

	private String id = UUID.randomUUID().toString();

	private long serviceId;

	private List<String> args = null;

	private List<String> inputs = null;

	private List<String> outputs = null;

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
		if (args == null)
			return Collections.emptyList();
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

	public void addArg(String arg) {
		if (this.args == null)
			this.args = new ArrayList<String>();
		this.args.add(arg);
	}
}

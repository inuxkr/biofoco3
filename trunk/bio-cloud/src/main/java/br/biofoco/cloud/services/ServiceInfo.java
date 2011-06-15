package br.biofoco.cloud.services;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

public class ServiceInfo {

	private long id;
	private String name;
	private List<String> arguments;
	private List<String> input;
	private List<String> output;
	private String info;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
	public List<String> getInput() {
		return input;
	}
	public void setInput(List<String> input) {
		this.input = input;
	}
	public List<String> getOutput() {
		return output;
	}
	public void setOutput(List<String> output) {
		this.output = output;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}	
	
	@Override
	public int hashCode() {
		return Longs.hashCode(id);
	}
	
	@Override
	public boolean equals(Object object) {
		
		if (this == object)
			return true;
		if (!(object instanceof ServiceInfo))
			return false;
		
		ServiceInfo other = (ServiceInfo) object;
		
		return id == other.id;
	}
	
	@Override
	public String toString() {
		
		return Objects.toStringHelper(ServiceInfo.class)
			   .add("id", id)
			   .add("name", name)
			   .toString();		
	}
	
}

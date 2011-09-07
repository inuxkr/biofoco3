package br.unb.cic.bionimbus.config;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.base.Objects;

import br.unb.cic.bionimbus.p2p.Host;

public class BioNimbusConfig {
	
	private @JsonIgnore String id;
	
	private @JsonIgnore Set<Host> seeds = new HashSet<Host>();

	private Host host;
	
	private boolean client = false;

	public boolean isClient() {
		return client;
	}
	
	public String getInfra() {
		return "hadoop";
	}
	
	public void setHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}

	public void setClient(boolean client) {
		this.client = client;
	}

	public void setSeeds(Set<Host> seeds) {
		this.seeds = seeds;
	}

	public Set<Host> getSeeds() {
		return seeds;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
	                  .add("id", id)	              
	                  .add("client", client)
	                  .add("host", host)
	                  .add("seeds", seeds)
		              .toString();
	}
}

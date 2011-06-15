/**
 * Copyright (C) 2011 University of Brasilia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.biofoco.p2p.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.biofoco.p2p.core.EndPoint;
import br.biofoco.p2p.core.ID;

import com.beust.jcommander.Parameter;

public class PeerConfig {
	
	public static final int DEFAULT_PORT = 9191;
	
	@Parameter(names="-persistent")
	private boolean isPersistent;
	
	@Parameter(names="-port", required=true)
	private int port = DEFAULT_PORT;
	
	@Parameter(names="-seed")
	private final List<String> seeds = new ArrayList<String>();
		
	@Parameter(names="-debug")
	private boolean debug = false;

	public List<String> getSeeds() {
		return seeds;
	}

	public int getPort() {
		return port;
	}
	
	public boolean isDebugEnabled() {
		return debug;
	}
	
	//TODO organize fields and methods
	private ID peerID = null;
	private final Set<EndPoint> endpoints = new HashSet<EndPoint>();

	private long uptime;
	
	public ID getPeerID() {
		return peerID;
	}
	
	public void setPeerID(ID peerID) {
		this.peerID = peerID;
	}
	
	public Collection<EndPoint> getEndPoints() {
		return endpoints;
	}
	
	public void add(EndPoint endpoint) {
		endpoints.add(endpoint);		
	}

	public void addEndPoints(Collection<EndPoint> endpoints) {
		this.endpoints.addAll(endpoints);		
	}

	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

	public boolean isPersistent() {
		return isPersistent;
	}

	public long getUptime() {
		return uptime;
	}
	
	public void setUptime(long time) {
		this.uptime = time;
	}
}

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

package br.biofoco.p2p.transport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import br.biofoco.p2p.config.PeerConfig;
import br.biofoco.p2p.core.OpenKad;

@Path("/")
public class HttpTransportResource {
	
	private final PeerConfig config;

	public HttpTransportResource() {
		config = OpenKad.get().getConfig();
	}
		
	@GET
	@Produces("text/plain")
	public String getRootPath() {
		String info = "{";
		info += "id: " + config.getPeerID();
		info += ",port:" + config.getPort();
//		info += ",uptime:" + (System.currentTimeMillis() - config.getUptime());
		info += "}";
		return info;
	}
	
	@GET
	@Path("/seeds")
	@Produces("text/plain")
	public String listPeers() {
		return "{\"seeds\":" + config.getSeeds().toString() + "}";
	}
	
	@GET
	@Path("peers")
	@Produces("text/plain")
	public String getNeighbours() {
		return "{neighbours:" + OpenKad.get().neighbours()+ "}";
	}

}

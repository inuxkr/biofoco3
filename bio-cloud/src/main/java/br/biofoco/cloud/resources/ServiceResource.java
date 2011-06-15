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
package br.biofoco.cloud.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.biofoco.cloud.services.ServiceManager;
import br.biofoco.cloud.utils.JsonUtil;

@Path("/")
public class ServiceResource {
	
	private final ServiceManager serviceManager = ServiceManager.getInstance();
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String greetings(){
		return this.toString();
	}

	@GET
	@Path("/services")
	@Produces(MediaType.TEXT_PLAIN)
	public String listServices() throws IOException {
		return JsonUtil.toString(serviceManager.listServices());
	}
	
	@GET
	@Path("/service/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String invokeService(@PathParam("id") String serviceID) {
		return serviceManager.invokeService(serviceID);		
	}
	
	@GET
	@Path("/service/result/{task}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getStatus(@PathParam("task") String taskID) {
		
		return serviceManager.getTaskResult(taskID);
	}
	
}

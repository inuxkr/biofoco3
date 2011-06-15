package br.biofoco.cloud.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.biofoco.cloud.jetty.JsonUtil;
import br.biofoco.cloud.services.ServiceManager;

@Path("/services")
public class ServiceResource {
	
	private final ServiceManager manager = ServiceManager.getInstance();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String listServices() throws IOException {
		return JsonUtil.toString(manager.listServices());
	}
	
}

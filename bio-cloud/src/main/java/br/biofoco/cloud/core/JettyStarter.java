package br.biofoco.cloud.core;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public final class JettyStarter {
	
	private Server server;

	public void start(int port) throws Exception {
		
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.biofoco.cloud.resources");

        server = new Server(port);
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        server.join();
	}
	
	public void stop() throws Exception {
        server.stop();
	}
	
//    Client c = Client.create();
//    WebResource r = c.resource("http://localhost:9999/");
//    System.out.println(r.get(String.class));
}

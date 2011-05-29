package br.biofoco.p2p.broker;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JettyStarter {
	
	public static void main(String[] args) throws Exception {
		
		Server server = new Server(7171);

		ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.biofoco.p2p.resources");
        
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
				
		server.start();
		server.join();
	}
}

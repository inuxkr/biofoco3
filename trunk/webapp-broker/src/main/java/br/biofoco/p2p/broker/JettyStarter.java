package br.biofoco.p2p.broker;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JettyStarter {
	
	private static final int DEFAULT_PORT = 7171;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JettyStarter.class);
	
	public static void main(String[] args) throws Exception {		
		startServer();
	}

	private static void startServer() throws Exception {
		
		LOGGER.debug("starting web server on port " + DEFAULT_PORT);
		
		Server server = new Server(DEFAULT_PORT);

		ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.biofoco.p2p.resources");
        
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
				
		server.start();
		server.join();
		
	}
}

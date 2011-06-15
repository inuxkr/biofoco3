package br.biofoco.cloud.jetty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JettyStarter {
	

	public static void main(String[] args) throws Exception {
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.biofoco.cloud.resources");

        Server server = new Server(9999);
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        server.join();

        Client c = Client.create();
        WebResource r = c.resource("http://localhost:9999/");
        System.out.println(r.get(String.class));

        server.stop();
	}
}

package br.unb.cic.bionimbus.p2p.plugin.proxy;

//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.mortbay.jetty.Server;
//import org.mortbay.jetty.servlet.Context;
//import org.mortbay.jetty.servlet.ServletHolder;
//
//import com.sun.jersey.spi.container.servlet.ServletContainer;


public class JettyRunner {
//
//	public JettyRunner() throws Exception {
//
//        ServletHolder sh = new ServletHolder(ServletContainer.class);
//
//		Server server = new Server(8080);
//
//		Context context = new Context(server, "/",Context.SESSIONS);
//
//        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
//        sh.setInitParameter("com.sun.jersey.config.property.packages", "br.unb.cic.bionimbus.broker.resources");
//
//        context.addServlet(sh, "/*");
//		server.start();
//		server.join();
//
////        Client c = Client.create();
////        WebResource r = c.resource("http://localhost:9999/");
////        System.out.println(r.get(String.class));
////		  server.stop();
//	}
//
//	public static void main(String[] args) throws Exception {
//
//		new JettyRunner();
//	}
//
//	public class MyServlet extends HttpServlet {
//
//		@Override
//		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//			resp.getWriter().write("Hello");
//		}
//
//	}

}

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

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class HttpTransportServer {

	private static final Logger LOGGER = Logger
			.getLogger(HttpTransportServer.class);

	private final int port;
	private Server server;
	private volatile boolean running = false;

	public HttpTransportServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {

		if (!running) {

			LOGGER.debug("Starting http transport on port " + port);

			ServletHolder sh = new ServletHolder(ServletContainer.class);

			sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
			sh.setInitParameter("com.sun.jersey.config.property.packages", "br.biofoco.p2p.transport");

			server = new Server(port);
			Context context = new Context(server, "/", Context.SESSIONS);
			context.addServlet(sh, "/*");
			server.start();
			running = true;

			// O join "prende" a thread corrente. Se tirar a linha abaixo, ele
			// fica assincrono.
			server.join();
		}
	}

	public void stop() throws Exception {
		
		if (running) {
			LOGGER.debug("Stopping http transport...");

			server.stop();
			running = false;
		}
	}

	public boolean isRunning() {
		return running;
	}

}

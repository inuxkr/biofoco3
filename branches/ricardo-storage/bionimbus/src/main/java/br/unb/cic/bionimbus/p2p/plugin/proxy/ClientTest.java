package br.unb.cic.bionimbus.p2p.plugin.proxy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ClientTest {
	
	public static void main(String[] args) throws Exception {
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		ProxyClientStub client = new ProxyClientStub("localhost", Proxy.PORT, executor );
				
		client.eventLoop();
		
		executor.shutdownNow();
	}

}

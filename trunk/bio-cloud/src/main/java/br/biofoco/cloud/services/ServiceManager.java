package br.biofoco.cloud.services;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.ImmutableSet;

public class ServiceManager {
	
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private final ConcurrentMap<Long, ServiceInfo> serviceMap = new ConcurrentHashMap<Long, ServiceInfo>(100);
	
	private static final String serviceDir = "services";
	
	private static ServiceManager INSTANCE = null;
	
	private ServiceManager() {
		start();
	}
	
	//TODO: refactor to use Guice singleton
	public static synchronized ServiceManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ServiceManager();
		
		return INSTANCE;
	}
		
	private void loadServices() throws IOException {
		
		File dir = new File(serviceDir);
		
		for (File file : dir.listFiles()){
			if (file.isFile() && file.canRead() && file.getName().endsWith(".json")) {
				ObjectMapper mapper = new ObjectMapper();
				ServiceInfo service = mapper.readValue(file, ServiceInfo.class);
				serviceMap.put(service.getId(), service);
			}
		}
	}
	
	public void start() {
		executor.scheduleWithFixedDelay(new ServiceInspector(), 0, 1, TimeUnit.MINUTES);
	}
	
	public void stop() {
		executor.shutdown();
	}
	
	public Set<ServiceInfo> listServices() {
		return ImmutableSet.copyOf(serviceMap.values());
	}
	
	private final class ServiceInspector implements Runnable {
		
		public void run() {
			try {
				loadServices();
			}
			catch (IOException ie) {			
				ie.printStackTrace();
			}	
		}
	}
}

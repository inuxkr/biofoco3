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
		executor.scheduleWithFixedDelay(new ServiceInspector(), 0, 30, TimeUnit.SECONDS);
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

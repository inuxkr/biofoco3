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

package br.biofoco.cloud.config;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class HostConfig {
	
	public static final int DEFAULT_PORT = 9191;
	
	@Parameter(names="-port")
	private int port = DEFAULT_PORT;
	
	@Parameter(names="-seeds")
	private final List<String> seeds = new ArrayList<String>();
		
	@Parameter(names="-debug")
	private boolean debug = false;

	public List<String> getSeeds() {
		return seeds;
	}

	public int getPort() {
		return port;
	}
	
	public boolean isDebugEnabled() {
		return debug;
	}

}

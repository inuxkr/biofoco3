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

package br.biofoco.p2p.config;

import static org.junit.Assert.*;

import org.junit.Test;

import com.beust.jcommander.JCommander;

public class JCommanderTest {
	
	@Test
	public void testHostSettings() {
		
		PeerConfig config = new PeerConfig();
		
		new JCommander(config, new String[]{"-seeds", "localhost:9090"
										   ,"-port", "9191"
										   ,"-debug"});
		
		assertEquals("localhost:9090", config.getSeeds().get(0));
		assertEquals(9191, config.getPort());
		assertTrue(config.isDebugEnabled());
	}
	
	

}

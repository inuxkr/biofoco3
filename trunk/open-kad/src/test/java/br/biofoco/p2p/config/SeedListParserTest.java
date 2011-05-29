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

import java.util.Collection;

import org.junit.Test;

public class SeedListParserTest {
	
	@Test
	public void testOneHostParsing() {
		String value = "localhost:8080, localhost:9090";
		Collection<HostEndPoint> seeds = new SeedListParser().convert(value);		
		assertEquals(2, seeds.size());
		
		assertTrue(seeds.contains(new HostEndPoint("localhost", 8080)));
		assertTrue(seeds.contains(new HostEndPoint("localhost", 9090)));
		
	}

}

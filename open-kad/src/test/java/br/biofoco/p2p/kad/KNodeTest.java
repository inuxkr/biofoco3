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

package br.biofoco.p2p.kad;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import br.biofoco.p2p.core.IDFactory;
import br.biofoco.p2p.core.PeerNode;

public class KNodeTest {
		
	private PeerNode peerNode;

	@Before
	public void setUp() {
		peerNode = mock(PeerNode.class);
		when(peerNode.getPeerID()).thenReturn(IDFactory.newRandomID());
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidTimestamp() {
		new KNode(peerNode, -1L);
	}
	
	@Test(expected=NullPointerException.class)
	public void nullIdInsertion() {
		new KNode(null, 1L);
	}
	
	@Test
	public void equality() {
		KNode kNode1 = new KNode(peerNode, 1L);
		KNode kNode2 = new KNode(peerNode, 2L);
		
		assertEquals(kNode1, kNode2);
	}
	
	@Test
	public void insertIntoATreeSet() {		
		SortedSet<KNode> set = new TreeSet<KNode>();
		KNode kNode1 = new KNode(peerNode, 8L);
		KNode kNode2 = new KNode(peerNode, 3L);
		set.add(kNode1);
		
		System.out.println(set.contains(kNode2));
		System.out.println(set);
	}
}

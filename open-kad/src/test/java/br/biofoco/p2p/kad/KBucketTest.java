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
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import br.biofoco.p2p.core.IDFactory;
import br.biofoco.p2p.core.PeerNode;


public class KBucketTest {
	
	private KBucket bucket;
	private PeerNode peerNode;

	@Before
	public void setUp() {
		bucket = new KBucket();		
		peerNode = new PeerNode(IDFactory.newRandomID());
	}

	@Test
	public void newBucketCreation() {
		assertEquals(0, bucket.size());
	}
	
	@Test
	public void singleElementInsertion() {		
		
		bucket.add(peerNode);		
		assertEquals(1, bucket.size());		
		assertEquals(peerNode, bucket.head());
		
	}
	
	@Test
	public void exceedingLimitElementInsertion() {
		for (long i = 0; i < 2 * KBucket.MAX_SIZE; i++){
			bucket.add(new PeerNode(IDFactory.newRandomID()));			
		}
		assertEquals(KBucket.MAX_SIZE, bucket.size());
	}
	
	@Test(expected=NullPointerException.class)
	public void nullElementInsertion() {
		bucket.add(null);
	}
	
	@Test
	public void duplicateElementInsertion() {
		
		bucket.add(peerNode);		
		
		bucket.add(peerNode);
		
		assertEquals(1, bucket.size());		
	}
	
	@Test
	public void checkOrderingByTimestamp() {
		PeerNode p1 = new PeerNode(IDFactory.newRandomID());		
		PeerNode p2 = new PeerNode(IDFactory.newRandomID());
		
		bucket.add(p1);
		
		waitOneSecond();
		
		bucket.add(p2);
		
		Iterator<PeerNode> it = bucket.iterator();
		
		assertEquals(p1, it.next());
		assertEquals(p2, it.next());
	}
	
	@Test
	public void insertAndRefresh() {
		PeerNode p1 = new PeerNode(IDFactory.newRandomID());		
		PeerNode p2 = new PeerNode(IDFactory.newRandomID());
		
		bucket.add(p1);
		
		waitOneSecond();
		
		bucket.add(p2);
		
		assertEquals(p2, bucket.tail());
		
		bucket.refresh(p1);
		
		assertEquals(p1, bucket.tail());
	}
	
	private void waitOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void containsSingleElement() {
		bucket.add(peerNode);
		assertTrue(bucket.contains(peerNode));
	}
}

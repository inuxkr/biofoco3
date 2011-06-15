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

import java.math.BigInteger;

import br.biofoco.p2p.core.ID;
import br.biofoco.p2p.core.PeerNode;

public class KadRouting {

	public static final int BIT_SIZE = 160;
//	private static final int TIME_OUT = 60 * 1000;	
	
	private final KBucket[] table = new KBucket[BIT_SIZE];
	private final ID[] index = new ID[BIT_SIZE];
	
	private final KadProtocol kadMessenger;
		
	private final ID peerID;
	
	public KadRouting(ID peerID, KadProtocol messenger) {
		this.peerID = peerID;
		this.kadMessenger = messenger;
		
		initIndexAndRoutingTable();
	}
	
	private void initIndexAndRoutingTable() {
		final BigInteger two = new BigInteger("2");
		for (int i = 0; i < BIT_SIZE; i++) {
			index[i] = new ID(two.pow(i+1));
			table[i] = new KBucket();
		}
	}
	
	public void add(PeerNode peer) {
		
		// the own peer should never be inserted into the routing table
		if (peerID.equals(peer.getPeerID()))
			return;
		
		final ID distance = peerID.xor(peer.getPeerID());
		
		KBucket bucket = retrieveKBucket(distance);
		
		if (bucket.contains(peer)){
			bucket.refresh(peer);
		}		
		else {
			if (bucket.hasEmptySlots()){
				bucket.add(peer);
			}
			else {
				PeerNode head = bucket.head();
				if (responsive(head)){
					bucket.refresh(head);
				}
				else {
					bucket.remove(head);
					bucket.add(peer);
				}
			}	
		}		
	}
	
	private boolean responsive(PeerNode peer) {
		try {
			kadMessenger.ping(peer);
			return true;
		} catch (KadException e) {
			return false;
		}
	}

	private KBucket retrieveKBucket(ID distance) {
		int pos = -1;
		for (int i = 0; i < BIT_SIZE; i++) {
			if (distance.between(index[i], index[i+1])){
				pos = i;
				break;
			}
		}
		
		return table[pos];
	}	
}

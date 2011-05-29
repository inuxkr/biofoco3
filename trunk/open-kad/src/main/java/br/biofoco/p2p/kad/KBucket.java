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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.biofoco.p2p.core.PeerNode;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class KBucket implements Iterable<PeerNode> {
	
	public static final int MAX_SIZE = 20;
	
	private final List<KNode> nodes = new ArrayList<KNode>();

	public synchronized boolean add(PeerNode peerNode) {
		
		checkNotNull(peerNode);
		
		if (hasEmptySlots() && !contains(peerNode)){
			nodes.add(new KNode(peerNode, System.nanoTime()));
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean refresh(PeerNode peer) {
		
		for (Iterator<KNode> it = nodes.iterator(); it.hasNext(); ){
			PeerNode other = it.next().getPeerNode();
			if (peer.equals(other)) {
				it.remove();
				break;
			}
		}
		
		return add(peer);		
	}
	
	public synchronized int size() {
		return nodes.size();
	}

	@Override
	public synchronized Iterator<PeerNode> iterator() {
		
		return Iterables.transform(nodes, new Function<KNode,PeerNode>(){

			@Override
			public PeerNode apply(KNode knode) {
				return knode.getPeerNode();
			}
			
		}).iterator();
	}

	public synchronized boolean contains(PeerNode peerNode) {
		for (KNode node : nodes) {
			if (node.getPeerNode().equals(peerNode)){
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean hasEmptySlots() {
		return nodes.size() < MAX_SIZE;
	}
	
	public synchronized PeerNode head(){
		if (nodes.size() == 0)
			return null;
		
		return nodes.get(0).getPeerNode();
	}
	
	public synchronized PeerNode tail() {
		if (nodes.size() == 0)
			return null;
		
		if (nodes.size() == 1){
			return nodes.get(0).getPeerNode();
		}
		
		return Lists.reverse(nodes).get(0).getPeerNode();
	}
	
	public synchronized boolean remove(PeerNode peer) {
		for (Iterator<KNode> it = nodes.iterator(); it.hasNext(); ){
			PeerNode other = it.next().getPeerNode();
			if (peer.equals(other)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}

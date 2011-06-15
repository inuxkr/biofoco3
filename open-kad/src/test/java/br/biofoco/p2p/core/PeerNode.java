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

package br.biofoco.p2p.core;

import java.util.HashSet;
import java.util.Set;

public class PeerNode {
	
	private final ID id;
	
	private Set<EndPoint> endpoints = new HashSet<EndPoint>();
	
	public PeerNode(ID id) {
		this.id = id;
	}
	
	public ID getPeerID(){
		return id;
	}
	
	public Iterable<EndPoint> getEndPoints(){
		return endpoints;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		
		if (!(obj instanceof PeerNode)){
			return false;
		}
		
		PeerNode other = (PeerNode) obj;
		
		return id.equals(other.id);
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
	
}

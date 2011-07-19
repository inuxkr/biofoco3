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

package br.unb.cic.bionimbus.p2p.dht;

import java.io.Serializable;
import java.math.BigInteger;

public final class ID implements Comparable<ID>, Serializable {

	private static final long serialVersionUID = -8684130279915421775L;
	private final BigInteger id;
			
	ID(BigInteger data){
		this.id = data;
	}
	
	ID(byte[] data){
		data = clone(data);
		if (data == null)
			throw new IllegalArgumentException("data cannot be null!");
		this.id = new BigInteger(data).abs();
	}
	
	private byte[] clone(byte[] src){
		byte[] dst = new byte[src.length];
		System.arraycopy(src, 0, dst, 0, src.length);
		return dst;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (!(obj instanceof ID))
			return false;
		return ((ID)obj).id.equals(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id.toString(16);
	}
	
	public int compareTo(ID o) {
		return id.compareTo(o.id);
	}	
	
	public ID xor(ID other) {
		return new ID(this.id.xor(other.id));
	}

	/**
	 *
	 * @param val1 
	 * @param val2
	 * @return true if  val1 <= this < val2
	 */
	public boolean between(ID val1, ID val2) {		
		return (this.compareTo(val1) > 0) && (this.compareTo(val2) < 0);
	}

//	// operacoes tem que ser modulo 2^160 
//	public ID previous() {
//		return new ID(id.subtract(BigInteger.ONE));
//	}
//	
//	// operacoes tem que ser modulo 2^160 
//	public ID next() {
//		return new ID(id.add(BigInteger.ONE));
//	}
		
}

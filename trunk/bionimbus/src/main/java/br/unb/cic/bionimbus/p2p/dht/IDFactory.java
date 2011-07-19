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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public final class IDFactory {
	
	private static final Random random = new Random(System.nanoTime());
	private static MessageDigest md;
	
	static{
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private IDFactory() {}
	
	public static ID newID(byte[] input){
		
//		Preconditions.checkNotNull(input, "Seed data cannot be null!");	
		
		if (input.length == 0)
			throw new IllegalArgumentException("Seed data cannot be empty!");
		
		byte[] digested = digest(input);
		return new ID(digested);
	}
	
	public static ID fromString(String input){	
		return new ID(new BigInteger(input, 16).abs());
	}

	private static byte[] digest(byte[] input) {
		md.reset();
		md.update(input);
		return md.digest();
	}

	public static ID newRandomID() {
		BigInteger seed = new BigInteger("" + random.nextLong());
		return new ID(seed.abs());		
	}

}

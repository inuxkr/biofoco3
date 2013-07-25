/**
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package br.unb.cic.bionimbus.p2p;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.google.common.base.Preconditions;


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
		
		Preconditions.checkNotNull(input, "Seed data cannot be null!");	
		
		if (input.length == 0)
			throw new IllegalArgumentException("Seed data cannot be empty!");
		
		byte[] digested = digest(input);
		return new ID(new BigInteger(digested));
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

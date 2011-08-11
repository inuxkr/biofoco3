
package br.unb.cic.bionimbus.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.unb.cic.bionimbus.p2p.ID;

public final class Hashifier {

	private Hashifier() { }

	public static ID SHA1(byte[] seed) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(seed, 0, seed.length);
		sha1hash = md.digest();
		System.out.println(new BigInteger(sha1hash).toString(16));
		return new ID(new BigInteger(sha1hash));
	}

}

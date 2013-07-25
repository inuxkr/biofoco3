
package br.unb.cic.bionimbus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		md.update(seed, 0, seed.length);
		byte[] sha1hash = md.digest();
		System.out.println(new BigInteger(sha1hash).toString(16));
		return new ID(new BigInteger(sha1hash));
	}
	
	public static String hashContent(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buf = new byte[64 * 1024];
		
		FileInputStream fis = new FileInputStream(file);
		
		int read = 0;
		
		while ((read = fis.read(buf)) != -1) {
			md.update(buf, 0, read);
		}
		
		return new BigInteger(md.digest()).toString(16);
		
	}

}

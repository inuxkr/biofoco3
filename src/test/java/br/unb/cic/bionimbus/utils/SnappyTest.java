package br.unb.cic.bionimbus.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.xerial.snappy.Snappy;

/**
 * For large datasets, see SnappyOutputStream and SnappyInputStream
 *
 */
public class SnappyTest {

	@Test
	public void testSnappy() throws UnsupportedEncodingException, IOException {
		String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
				+ "Snappy, a fast compresser/decompresser.";
		byte[] compressed = Snappy.compress(input.getBytes("UTF-8"));
		byte[] uncompressed = Snappy.uncompress(compressed);

		String result = new String(uncompressed, "UTF-8");
		assertEquals(input, result);
	}
}

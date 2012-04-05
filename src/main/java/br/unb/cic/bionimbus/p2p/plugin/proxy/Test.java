package br.unb.cic.bionimbus.p2p.plugin.proxy;


public class Test {

	public static void main(String[] args) {
		String[] array = "a, b,  c, ,".split(",");

		for (String s : array) {
			s = s.trim();
			System.out.println(s);
		}
	}
}

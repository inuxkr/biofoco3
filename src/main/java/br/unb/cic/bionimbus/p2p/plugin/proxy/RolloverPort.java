package br.unb.cic.bionimbus.p2p.plugin.proxy;

public final class RolloverPort {
	
	private final int baseAddress;
	private final int mod;
	
	private int next;

	public RolloverPort(int baseAddress, int endAddress) {			
		this.baseAddress = next = baseAddress;			
		mod = endAddress - baseAddress + 1;
	}
	
	public synchronized int next() {
		int value = next;
		next = baseAddress + ((next - baseAddress + 1) % mod);
		return value;			
	}		
}

package br.unb.cic.bionimbus.p2p;


public enum P2PMessageType {
	
	INFOREQ(0x1),
	INFORESP(0x2),
	STARTREQ(0x3),
	STARTRESP(0x4),
	END(0x5),
	STATUSREQ(0x6),
	STATUSRESP(0x7),
	STOREREQ(0x8),
	STORERESP(0xA),
	GETREQ(0xB),
	GETRESP(0xC),
	CLOUDREQ(0xD),
	CLOUDRESP(0xE),
	SCHEDREQ(0xF),
	SCHEDRESP(0x11),
	JOBREQ(0x12),
	JOBRESP(0X13),
	ERROR(0x14),
	PINGREQ(0x15),
	PINGRESP(0x16);
	
	private final int code;
	
	private P2PMessageType(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}

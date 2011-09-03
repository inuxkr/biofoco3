package br.unb.cic.bionimbus.p2p;

import java.io.File;

public class P2PFileEvent implements P2PEvent {

	private File file;

	public P2PFileEvent(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public P2PEventType getType() {
		return P2PEventType.FILE;
	}

}

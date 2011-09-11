package br.unb.cic.bionimbus.p2p.messages;

import br.unb.cic.bionimbus.client.FileInfo;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.utils.JsonCodec;

public class StoreReqMessage extends AbstractMessage {
	
	private FileInfo fileInfo;
	
	public StoreReqMessage() {
		super();
	}
	
	public StoreReqMessage(PeerNode peer, FileInfo fileInfo) {
		super(peer);
		this.fileInfo = fileInfo;
	}
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	@Override
	public int getType() {
		return P2PMessageType.STOREREQ.code();
	}

	@Override
	public byte[] serialize() throws Exception {
		BulkMessage message = encodeBasicMessage();
		message.setFileInfo(fileInfo);
		return JsonCodec.encodeMessage(message);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		BulkMessage message = decodeBasicMessage(buffer);
		this.fileInfo = message.getFileInfo();
	}

}

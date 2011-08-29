package br.unb.cic.bionimbus.p2p.messages;

import java.util.StringTokenizer;

import com.google.common.base.Charsets;

import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.IDFactory;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.PeerNode;

public class PingRespMessage implements Message {

	private PeerNode peer;

	public PingRespMessage(PeerNode peerNode) {
		this.peer = peerNode;
	}
	
	public PingRespMessage() {
		
	}

	@Override
	public byte[] serialize() throws Exception {
		String raw = peer.getId().toString() + ":" + peer.getHost().getAddress() + ":" + peer.getHost().getPort();
		return raw.getBytes(Charsets.UTF_8);
	}

	@Override
	public void deserialize(byte[] buffer) throws Exception {
		String raw = new String(buffer, Charsets.UTF_8);
		StringTokenizer st = new StringTokenizer(raw, ":");
		String id = st.nextToken();
		String address = st.nextToken();
		int port = Integer.parseInt(st.nextToken());
		peer = new PeerNode(IDFactory.fromString(id));
		peer.setHost(new Host(address, port));
	}

	@Override
	public int getType() {
		return P2PMessageType.PINGRESP.ordinal();
	}

	public PeerNode getPeerNode() {
		return peer;
	}

}

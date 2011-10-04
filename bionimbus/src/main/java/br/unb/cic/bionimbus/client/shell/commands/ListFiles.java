package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.shell.Command;
import br.unb.cic.bionimbus.client.shell.SimpleShell;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PEventType;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PMessageEvent;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.messages.ListReqMessage;
import br.unb.cic.bionimbus.p2p.messages.ListRespMessage;
import br.unb.cic.bionimbus.plugin.PluginFile;

public class ListFiles implements Command {

	public static final String NAME = "files";

	private final SimpleShell shell;

	public ListFiles(SimpleShell shell) {
		this.shell = shell;
	}

	@Override
	public String execute(String... params) throws Exception {

		if (!shell.isConnected())
			throw new IllegalStateException(
					"This command should be used with an active connection!");

		P2PService p2p = shell.getP2P();
		Communication comm = new Communication(p2p);

		shell.print("Listing files...");

		comm.sendReq(new ListReqMessage(p2p.getPeerNode()),
				P2PMessageType.LISTRESP);
		ListRespMessage resp = (ListRespMessage) comm.getResp();

		String list = "";
		for (PluginFile file : resp.values()) {
			list += "ID: " + file.getId() + "; NAME: " + file.getPath() + "; SIZE: " + file.getSize() + "\n";
		}
		list += "\n";

		return list;
	}

	@Override
	public String usage() {
		return NAME;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setOriginalParamLine(String param) {
	}

	private class Communication implements Runnable, P2PListener {

		private final P2PService p2p;
		private Message reqMsg = null;
		private Message respMsg = null;
		private P2PMessageType respType = null;

		public Communication(P2PService p2p) {
			this.p2p = p2p;
		}

		@Override
		public void onEvent(P2PEvent event) {
			if (event.getType().equals(P2PEventType.MESSAGE)) {
				P2PMessageEvent msgEvent = (P2PMessageEvent) event;
				Message recvdMsg = msgEvent.getMessage();
				if (recvdMsg.getType() == respType.code())
					putResp(recvdMsg);
			}
		}

		@Override
		public void run() {
			p2p.broadcast(reqMsg);
		}

		private synchronized void putResp(Message respMsg) {
			p2p.remove(this);
			this.respMsg = respMsg;
			notify();
		}

		public synchronized Message getResp() throws InterruptedException {
			while (respMsg == null)
				wait();
			return respMsg;
		}

		public void sendReq(Message reqMsg, P2PMessageType respType) {
			this.reqMsg = reqMsg;
			this.respType = respType;
			p2p.addListener(this);
			Thread t = new Thread(this);
			t.start();
		}

	}

}

package br.unb.cic.bionimbus.p2p.dht;

import java.io.IOException;
import java.util.Set;

import br.unb.cic.bionimbus.p2p.transport.Command;


public final class PeerServer {
	
	private final int port;	
	private MessengerService messenger;
	private DHTService dhtService;
	
	private PeerServer(PeerConfig config) throws IOException {
		this.port = config.getPort();
		this.messenger = new MessengerService(config);
		messenger.addCommand(new ServiceCommand());
		dhtService = new DHTService(config, messenger);
	}

	public static PeerServer newPeerServer(PeerConfig config) throws IOException {
		return new PeerServer(config);
	}

	public void startServices() throws IOException {
		messenger.start();
		dhtService.start();
	}
	
	public int getPort(){
		return port;
	}
	
	private class ServiceCommand implements Command {

		@Override
		public String getName() {
			return "SERVICE";
		}

		@Override
		public String execute() throws Exception {
			return "BLAST:Interpro";
		}
		
	}

	public Set<PeerNode> getPeerView() {
		return dhtService.getPeerView();
	}

}

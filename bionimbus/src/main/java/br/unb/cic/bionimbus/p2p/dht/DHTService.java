package br.unb.cic.bionimbus.p2p.dht;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.unb.cic.bionimbus.p2p.dht.walker.PeerView;
import br.unb.cic.bionimbus.p2p.transport.Command;
import br.unb.cic.bionimbus.p2p.transport.PeerCredentials;

public class DHTService {
	
	private final MessengerService messenger;
	private final PeerView peerView = new PeerView();
	
	private final PeerNode peerNode;
	
	private final Set<Endpoint> seeds = new HashSet<Endpoint>();
	
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
	
	private static final Logger LOG = LoggerFactory.getLogger(DHTService.class);

	public DHTService(PeerConfig config, MessengerService messenger) throws IOException {
		
		this.messenger = messenger;
		messenger.addCommand(new PingCommand());
		
		this.peerNode = new PeerNode(config.getPeerID());
		Endpoint endpoint = new Endpoint(InetAddress.getLocalHost().getHostAddress(), config.getPort());
		this.peerNode.setEndpoint(endpoint);
		
		peerView.add(peerNode);
		
		for (Endpoint host : config.getSeeds()){
			seeds.add(host);
		}
		
		System.out.println("seeds: " + seeds);
		
		executor.scheduleAtFixedRate(new HostProber(seeds), 30, 60, TimeUnit.SECONDS);
	}
		
	public void start() throws IOException {
		LOG.debug("starting peer " + peerNode.getId());	
		if (!messenger.isRunning())
			messenger.start();
	}
	
	private class HostProber implements Runnable {
		
		private Set<Endpoint> probeList = new HashSet<Endpoint>();

		public HostProber(Set<Endpoint> seeds) {
			probeList.addAll(seeds);
		}

		@Override
		public void run() {
			if (probeList.size() > 0) {				
				System.out.println("Probing seeds");
				for (Iterator<Endpoint> it = probeList.iterator(); it.hasNext(); ){
					
					Endpoint host = it.next();
					
					PeerNode node = ping(host);
					
					if (node != null){
						peerView.add(node);
						it.remove();
					}					
				}				
			}
		}

		private PeerNode ping(Endpoint host) {
			
			PeerNode p = null;
			try {
				p = messenger.ping(host);
				peerView.add(p);
				
				System.out.println("peer successfully discovered: " + p);
			} catch (Exception me) {
				//do nothing
				System.out.println("Error probing peer!");
			} 
			return p;
		}
		
	}
	
	private class PingCommand implements Command {

		@Override
		public String getName() {
			return "PING";
		}

		@Override
		public String execute() throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			PeerCredentials creds = new PeerCredentials();
			creds.setId(peerNode.getId().toString());
			creds.setEndpoint(peerNode.getEndpoint());
			return mapper.writeValueAsString(creds);
		}		
	}

	public Set<PeerNode> getPeerView() {
		return peerView.getPeerView();
	}

}

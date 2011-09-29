package br.unb.cic.bionimbus.client;

import java.io.IOException;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.config.BioNimbusConfigLoader;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.PeerNode;
import br.unb.cic.bionimbus.p2p.messages.CloudReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;

public class Client implements P2PListener {

	private P2PService p2p;

	public void setP2P(P2PService p2p) {
		this.p2p = p2p;
		if (p2p != null)
			p2p.addListener(this);
	}

	public void listServices() {
		Message message = new CloudReqMessage(p2p.getPeerNode());		
		p2p.broadcast(message);
	}

	public void startJob(PeerNode node) {
		
		JobInfo job = new JobInfo();
		job.setId(null);
		//job.setArgs(null);
		job.addArg("--help");
		job.setServiceId(1023296285);
		job.setInputs(null);
		
		JobReqMessage msg = new JobReqMessage(p2p.getPeerNode(), job);
		p2p.sendMessage(node.getHost(), msg);
		
	}
	
	/*public void jobStatus(String jobId) {
		
	}*/

	@Override
	public void onEvent(P2PEvent event) {
		System.out.println("recebi resposta da nuvem");
		p2p.shutdown();
	}

	public static void main(String[] args) throws IOException {
		
		String configFile = System.getProperty("config.file", "conf/client.json");		
		BioNimbusConfig config = BioNimbusConfigLoader.loadHostConfig(configFile);
				
		P2PService p2p = new P2PService(config);
		p2p.start();

		Client client = new Client();
		client.setP2P(p2p);
		
		while (p2p.getPeers().size() == 0) {}
		System.out.println("I am not alone in the dark anymore!");
		
		PeerNode node = p2p.getPeers().get(0);
			
		client.startJob(node);
	}

}

package br.unb.cic.bionimbus.client;

import br.unb.cic.bionimbus.config.BioNimbusConfig;
import br.unb.cic.bionimbus.messaging.Message;
import br.unb.cic.bionimbus.p2p.Host;
import br.unb.cic.bionimbus.p2p.P2PEvent;
import br.unb.cic.bionimbus.p2p.P2PListener;
import br.unb.cic.bionimbus.p2p.P2PService;
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
		Message message = new CloudReqMessage();
		p2p.sendMessage(new Host("localhost", 9999), message);
	}

	public void startJob() {
		JobInfo job = new JobInfo();
		job.setId(null);
		job.setArgs(null);
		job.setServiceId(1023296285);
		job.setInputs(null);
		
		JobReqMessage msg = new JobReqMessage(job);
		p2p.sendMessage(msg);
	}

	@Override
	public void onEvent(P2PEvent event) {
		System.out.println("recebi resposta da nuvem");
		p2p.shutdown();
	}

	public static void main(String[] args) {
		BioNimbusConfig config = new BioNimbusConfig();
		config.setHost(new Host("localhost", 8080));

		P2PService p2p = new P2PService();
		p2p.setConfig(config);
		p2p.start();

		Client client = new Client();
		client.setP2P(p2p);
		client.startJob();
	}

}

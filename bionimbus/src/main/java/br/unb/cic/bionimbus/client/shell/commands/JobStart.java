package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.JobInfo;
import br.unb.cic.bionimbus.client.shell.Command;
import br.unb.cic.bionimbus.client.shell.SimpleShell;
import br.unb.cic.bionimbus.p2p.P2PMessageType;
import br.unb.cic.bionimbus.p2p.P2PService;
import br.unb.cic.bionimbus.p2p.messages.JobReqMessage;
import br.unb.cic.bionimbus.p2p.messages.JobRespMessage;

public class JobStart implements Command {
	
	public static final String NAME = "start";
	
	private final SimpleShell shell;
	
	public JobStart(SimpleShell shell) {
		this.shell = shell;
	}

	@Override
	public String execute(String... params) throws Exception {
		if (!shell.isConnected())
			throw new IllegalStateException(
					"This command should be used with an active connection!");

		P2PService p2p = shell.getP2P();
		SyncCommunication comm = new SyncCommunication(p2p);

		shell.print("Starting job...");

		JobInfo job = new JobInfo();
		job.setId(null);
		job.setServiceId(Long.parseLong(params[0]));

		if (params.length > 1) {
			job.setArgs(params[1]);
			if (params.length > 2) {
				int i = 2;
				if (params[i].equals("-i")) {
					i++;
					while (i < params.length && !params[i].equals("-o")) {
						job.addInput(params[i], Long.valueOf(0));
						i++;
					}
				}
				if (params[i].equals("-o")) {
					i++;
					while (i < params.length) {
						job.addOutput(params[i]);
						i++;
					}
				}
			}
		}
		
		comm.sendReq(new JobReqMessage(p2p.getPeerNode(), job), P2PMessageType.JOBRESP);
		JobRespMessage resp = (JobRespMessage) comm.getResp();
		
		return "Job " + resp.getJobInfo().getId() + " started succesfully";
	}

	@Override
	public String usage() {
		return NAME + " <serviceId> [args [-i inputs] [-o outputs]]";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setOriginalParamLine(String param) {
	}

}

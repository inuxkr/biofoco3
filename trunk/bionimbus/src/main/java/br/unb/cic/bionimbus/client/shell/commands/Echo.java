package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.shell.Command;

public class Echo implements Command {

	@Override
	public String execute(String... params) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String p : params) {
			sb.append(p);
			sb.append(" ");
		}
		return sb.toString();
	}

	@Override
	public String usage() {
		return "echo";
	}

	@Override
	public String getName() {
		return "echo";
	}

}

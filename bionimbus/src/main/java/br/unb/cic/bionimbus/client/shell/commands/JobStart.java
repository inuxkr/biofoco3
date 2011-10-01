package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.shell.Command;

public class JobStart implements Command {
	
	public static final String NAME = "start";

	@Override
	public String execute(String... params) throws Exception {
		return "not implemented yet";
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

}

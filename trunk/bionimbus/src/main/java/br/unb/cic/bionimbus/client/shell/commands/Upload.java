package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.shell.Command;

public class Upload implements Command {
	
	public static final String NAME = "upload";

	@Override
	public String execute(String... params) throws Exception {
		return "not implemented yet";
	}

	@Override
	public String usage() {
		return NAME + " <filepath>";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setOriginalParamLine(String param) {
	}

}

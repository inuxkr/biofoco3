package br.unb.cic.bionimbus.client.shell.commands;

import br.unb.cic.bionimbus.client.shell.Command;
import br.unb.cic.bionimbus.client.shell.SimpleShell;

public class ListFiles implements Command {
	
	public static final String NAME = "files";
	
	private final SimpleShell shell;
	
	public ListFiles(SimpleShell shell) {
		this.shell = shell;
	}

	@Override
	public String execute(String... params) throws Exception {
		
		shell.print("Listing files...");
		
		return "";
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

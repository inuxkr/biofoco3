package br.unb.cic.bionimbus.client.shell.commands;

import java.util.Map;

import br.unb.cic.bionimbus.client.shell.Command;

public class Help implements Command {
	
	public static final String NAME = "help";	
	private Map<String, Command> commands;

	public Help(Map<String,Command> commands) {
		this.commands = commands;
	}

	@Override
	public String execute(String... params) {
		
		StringBuilder sb = new StringBuilder();
		
		for (Command c : commands.values()){
			sb.append(c.usage()).append('\n');
		}
		
		return sb.toString();
	}

	@Override
	public String usage() {
		return "help";
	}
	
	public String getName() {
		return NAME;
	}

}

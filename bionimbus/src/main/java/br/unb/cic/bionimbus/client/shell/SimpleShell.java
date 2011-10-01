package br.unb.cic.bionimbus.client.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import br.unb.cic.bionimbus.client.shell.commands.AsyncCommand;
import br.unb.cic.bionimbus.client.shell.commands.DateTime;
import br.unb.cic.bionimbus.client.shell.commands.Echo;
import br.unb.cic.bionimbus.client.shell.commands.Help;
import br.unb.cic.bionimbus.client.shell.commands.History;
import br.unb.cic.bionimbus.client.shell.commands.ListFiles;
import br.unb.cic.bionimbus.client.shell.commands.Quit;
import br.unb.cic.bionimbus.client.shell.commands.ScriptRunner;
import br.unb.cic.bionimbus.utils.Pair;

/**
 * A simple shell to interface with BioNimbus.
 * Lacking features:
 *    No up arrow down arrow (circular buffer)
 *    No pipe | or redirection > 
 *    No autocomplete
 *
 */
public final class SimpleShell {

	private static final String GREETINGS = "Welcome to BioNimbus shell\nversion 1.0";
	private static final String PROMPT = "[@bionimbus]$ ";

	private static final Map<String, Command> commandMap = new HashMap<String, Command>();
	
	public static History history = new History(10);
	
	static {
		commandMap.put(DateTime.NAME, new DateTime());
		commandMap.put(Quit.NAME, new Quit());
		commandMap.put(Help.NAME, new Help(commandMap));
		commandMap.put(History.NAME, history);
		commandMap.put(Echo.NAME, new Echo());
	}
	
	public SimpleShell() {
		commandMap.put("async", new AsyncCommand(this));
		commandMap.put("script", new ScriptRunner(this));
		commandMap.put(ListFiles.NAME, new ListFiles(this));
	}
	
	public void registerCommand(Command command){
		commandMap.put(command.getName(), command);
	}

	public static void main(String[] args) throws IOException {
		new SimpleShell().readEvalPrintLoop();
	}
	
	public void print(String message){
		System.out.println('\n' + message);
		System.out.print(PROMPT);
	}

	private void readEvalPrintLoop() throws IOException {
		
		System.out.println(GREETINGS);
		
		while (true) {

			// read
			System.out.print(PROMPT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String line = reader.readLine().trim();
			
			if (line.startsWith("!")){
				Long number = Long.parseLong(line.substring(1));					
				line = history.get(number);
			}
			
			executeCommand(line, true);
		}
	}

	public void executeCommand(String line, boolean logAtHistory) {
		
		if (logAtHistory)
			history.add(line.trim());
		
		Pair<String, String[]> command = parseLine(line);
		
		if (!commandMap.containsKey(command.first)) {
			System.out.println(String.format("%s: command not found", command.first));
		} else {
			try {
				
				commandMap.get(command.first).setOriginalParamLine(line); // para o caso de precisar
				
				// eval
				String result = commandMap.get(command.first).execute(command.second);
				
				// print
				System.out.println(result);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private static Pair<String, String[]> parseLine(String line) {
		
		final List<String> params = new ArrayList<String>();
		boolean first = true;
		String command = null;
		
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {			
		
			String s = st.nextToken().trim();
			if (first) {
				command = s;
				first = false;
			}
			else 
				params.add(s);
		}
		return Pair.of(command, params.toArray(new String[0]));
	}

}

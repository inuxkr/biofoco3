package br.unb.cic.bionimbus.client.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import br.unb.cic.bionimbus.client.shell.commands.AsyncCommand;
import br.unb.cic.bionimbus.client.shell.commands.Connect;
import br.unb.cic.bionimbus.client.shell.commands.DateTime;
import br.unb.cic.bionimbus.client.shell.commands.Echo;
import br.unb.cic.bionimbus.client.shell.commands.Help;
import br.unb.cic.bionimbus.client.shell.commands.History;
import br.unb.cic.bionimbus.client.shell.commands.JobCancel;
import br.unb.cic.bionimbus.client.shell.commands.JobStart;
import br.unb.cic.bionimbus.client.shell.commands.ListCommands;
import br.unb.cic.bionimbus.client.shell.commands.ListFiles;
import br.unb.cic.bionimbus.client.shell.commands.ListServices;
import br.unb.cic.bionimbus.client.shell.commands.Quit;
import br.unb.cic.bionimbus.client.shell.commands.ScriptRunner;
import br.unb.cic.bionimbus.client.shell.commands.Upload;
import br.unb.cic.bionimbus.p2p.P2PService;
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
	
	private boolean connected = false;
	private P2PService p2p = null;
	
	public SimpleShell() {
		commandMap.put(Connect.NAME, new Connect(this));
		commandMap.put("async", new AsyncCommand(this));
		commandMap.put("script", new ScriptRunner(this));
		commandMap.put(ListFiles.NAME, new ListFiles(this));
		commandMap.put(Upload.NAME, new Upload(this));
		commandMap.put(ListServices.NAME, new ListServices(this));
		commandMap.put(JobStart.NAME, new JobStart(this));
		commandMap.put(JobCancel.NAME, new JobCancel(this));
		commandMap.put(ListCommands.NAME, new ListCommands(this));
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
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setP2P(P2PService p2p) {
		this.p2p = p2p;
	}
	
	public P2PService getP2P() {
		return p2p;
	}

	private static Pair<String, String[]> parseLine(String line) {
		
		LineParser parser = new LineParser();		
		List<String> tokens = parser.parse(line);
		
		final String command = tokens.get(0);
		final List<String> params = new ArrayList<String>();
		
		if (tokens.size() > 1){
			params.addAll(tokens.subList(1, tokens.size()));
		}
		
		return Pair.of(command, params.toArray(new String[0]));
	}

	public Collection<Command> getCommands() {
		// TODO Auto-generated method stub
		return new HashSet<Command>(commandMap.values());
	}

}

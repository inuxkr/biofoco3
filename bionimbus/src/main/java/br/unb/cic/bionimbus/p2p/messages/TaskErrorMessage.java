package br.unb.cic.bionimbus.p2p.messages;

public class TaskErrorMessage extends ErrorMessage {
	
	private String id;
	
	public TaskErrorMessage(String id, String description) {
		super(description);
		this.id = id;
	}

}

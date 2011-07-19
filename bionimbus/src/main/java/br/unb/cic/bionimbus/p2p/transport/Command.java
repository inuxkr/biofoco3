package br.unb.cic.bionimbus.p2p.transport;

public interface Command {

	String getName();

	String execute() throws Exception;

}

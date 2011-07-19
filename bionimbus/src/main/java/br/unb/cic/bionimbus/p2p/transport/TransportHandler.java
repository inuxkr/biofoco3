package br.unb.cic.bionimbus.p2p.transport;

public interface TransportHandler {

	WireMessage doRequestResponse(WireMessage command);
}

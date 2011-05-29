package br.biofoco.p2p.broker.httpclient;

public interface EventListener {

	public void onMessage(BrokerEvent event);
}

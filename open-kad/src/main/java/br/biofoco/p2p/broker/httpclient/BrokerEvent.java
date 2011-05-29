package br.biofoco.p2p.broker.httpclient;

public class BrokerEvent {

	private final EventType event;
	private final String data;

	public BrokerEvent(EventType event, String data) {
		this.event  = event;
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public EventType getEventType(){
		return event;
	}
	
	@Override
	public String toString() {
		return event + ":" + data;
	}

}

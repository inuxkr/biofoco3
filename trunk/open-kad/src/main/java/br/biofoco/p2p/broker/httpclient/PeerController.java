package br.biofoco.p2p.broker.httpclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

@SuppressWarnings("unused")
public class PeerController {
	
	private final ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
	private final HttpAdapter adapter = new HttpAdapter();
	private final CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();
	
//	private static final String MAIL_ADDR = "http://1.5.p2p-rendezvous.appspot.com/mailbox/";
//	private static final String PING_ADDR = "http://1.5.p2p-rendezvous.appspot.com/mailbox/ping/";
	
	private static final String MAIL_ADDR = "http://localhost:7171/mailbox/";
	private static final String PING_ADDR = "http://localhost:7171/ping/";
	
	private final long id;
	
	public PeerController(long id) {
		this.id = id;
	}
	
	public void start() {
		service.scheduleWithFixedDelay(new PingCommand(), 0, 2, TimeUnit.MINUTES);
		service.scheduleWithFixedDelay(new WhoIsCommand(), 0, 2, TimeUnit.MINUTES);
		service.scheduleWithFixedDelay(new CheckMailbox(), 0, 2, TimeUnit.MINUTES);
	}
	
	public void sendMessage(String dest, String message) {
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("sender", "" + id);
		map.put("receiver", dest);
		map.put("body", message);
		
		String url = MAIL_ADDR + dest;
		
		System.out.println(url);
		
		try {
			adapter.doPost(url, map);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void stop() {
		service.shutdownNow();
	}
	
	public void addListener(EventListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(EventListener listener){
		return listeners.remove(listener);
	}

	private class PingCommand implements Runnable {

		@Override
		public void run() {
						
			try {
				System.out.println("Pinging broker");
				Map<String,String> map = new HashMap<String,String>();
				map.put("id", "" + id);
				adapter.doPost(PING_ADDR, map);				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}		
	}
	
	private class WhoIsCommand implements Runnable {

		@Override
		public void run() {			
			try {
				System.out.println("Checking on-line peers");
				String result = adapter.doGet(PING_ADDR);
				notifyListeners(EventType.PRESENCE, result);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}		
		}	
	}
	
	private class CheckMailbox implements Runnable {

		@Override
		public void run() {
			try {
				String result = adapter.doGet(MAIL_ADDR + id);
				notifyListeners(EventType.MESSAGE, result);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}		
	}
	

	private void notifyListeners(EventType eventType, String data) {
		for (EventListener l : listeners) {
			BrokerEvent e = new BrokerEvent(eventType, data);
			l.onMessage(e);
		}
	}	

	public long getID() {
		return id;
	}
}

package br.biofoco.p2p.broker.httpclient;

import static java.lang.Math.abs;

import java.util.Random;

public class PeerControllerTest implements EventListener {
	
	public PeerControllerTest() {
		
	}

	public static void main(String[] args) {
		
		PeerController controller = new PeerController(abs(new Random(System.nanoTime()).nextLong()));
		controller.addListener(new PeerControllerTest());
		controller.start();
		
		int i = 0;

		while (i < 10) {
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			controller.sendMessage("" + controller.getID(), "Hello, Buddy, salt=" + i);
			i++;
		}
	}

	@Override
	public void onMessage(BrokerEvent event) {
		System.out.println(event.toString());
	}

}

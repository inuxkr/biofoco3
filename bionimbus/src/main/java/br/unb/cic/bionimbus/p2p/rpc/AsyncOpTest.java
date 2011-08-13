package br.unb.cic.bionimbus.p2p.rpc;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;


public class AsyncOpTest extends TestCase {
	
	public static void main(String[] args) throws InterruptedException {
				
		AsyncOp<String> a = new AsyncOp<String>(new AsyncOpListener<String>() {
			
			@Override
			public void onTimeoutException() {
				System.out.println("tempo expirou!");
			}
			
			@Override
			public void onFinished(String result) {
				System.out.println("Resultado: " + result);
			}

			@Override
			public void onInterruptedException() {
				System.out.println("interrupted exception");
				
			}
		}, 5 * 1000);
		
		a.start();
		
//		a.cancel();
		
		TimeUnit.SECONDS.sleep(2);				
//		a.completed("hello");
		
	}
	
}

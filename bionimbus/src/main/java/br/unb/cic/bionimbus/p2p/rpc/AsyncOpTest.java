package br.unb.cic.bionimbus.p2p.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;


public class AsyncOpTest extends TestCase {
	
	public static void main(String[] args) throws InterruptedException {
				
		AsyncOp<String> a = new AsyncOp<String>(
		new Callable<String>() {

			@Override
			public String call() throws Exception {
				System.out.println("executing");
				return "OK";
			}
			
		}
		,new AsyncOpListener<String>() {
			
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
		}
//		,Executors.newCachedThreadPool()
		, 3 * 1000);
		
		a.start();
		
		System.out.println("async operation dispatched");
		
//		a.cancel();
		
		TimeUnit.SECONDS.sleep(2);				
		a.completed("hello");
		
	}
	
}

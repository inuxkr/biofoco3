package br.unb.cic.bionimbus.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import junit.framework.TestCase;


public class AsyncOpTest extends TestCase {
	
	public static void main(String[] args) throws Exception {
		
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
		builder.setDaemon(true).setNameFormat("async");
		
		ExecutorService executor = Executors.newCachedThreadPool(builder.build());
				
		AsyncOp<String> a = new AsyncOp<String>(new Callable<String>() {

			@Override
			public String call() throws Exception {
				TimeUnit.SECONDS.sleep(10000);
				System.out.println("executing");
				return "OK";
			}
			
		}
		,executor
		, 3 * 1000);
		
		// waiting for result
		final ListenableFuture<String> listener = a.execute();
		listener.addListener(new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("answer: " + listener.get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		}, executor);
		
//		while (!Sets.newHashSet(AsyncOpStatus.FINISHED, AsyncOpStatus.TIMED_OUT).contains(a.getStatus())) {}
//		
//		System.out.println(Long.toString(a.elapsedTimeInMilliseconds()));
//		
//		System.out.println("async operation dispatched");
		
		
	}
	
}

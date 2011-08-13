package br.unb.cic.bionimbus.p2p.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;

import junit.framework.TestCase;


public class AsyncOpTest extends TestCase {
	
	public static void main(String[] args) throws Exception {
				
		AsyncOp<String> a = new AsyncOp<String>(
		new Callable<String>() {

			@Override
			public String call() throws Exception {
				TimeUnit.MINUTES.sleep(1);
				System.out.println("executing");
				return "OK";
			}
			
		}
		,Executors.newCachedThreadPool()
		, 3 * 1000);
		
		a.execute();
		
		while (!Sets.newHashSet(AsyncOpStatus.FINISHED, AsyncOpStatus.TIMED_OUT).contains(a.getStatus())) {}
		
		System.out.println(Long.toString(a.elapsedTimeInMilliseconds()));
		
		System.out.println("async operation dispatched");
		
		
	}
	
}

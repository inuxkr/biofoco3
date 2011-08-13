package br.unb.cic.bionimbus.utils;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

public class AsyncOpTest2 {
	
	private static int value1;
	private static int value2;
	
	public static void main(String[] args) {
		
		
		
		AsyncOpFactory factory = new AsyncOpFactory(Executors.newCachedThreadPool(), 2 * 60 * 1000);
		
		final CyclicBarrier barrier = new CyclicBarrier(2);
		
		AsyncOp<Integer> op1 = factory.create(new Operation());
		
		final ListenableFuture<Integer> future1 = op1.execute();
		
		future1.addListener(new Runnable() {

			@Override
			public void run() {
				try {
					value1 = future1.get();
					System.out.println(value1);
					barrier.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}, Executors.newCachedThreadPool());
		
		AsyncOp<Integer> op2 = factory.create(new Operation());
		
		final ListenableFuture<Integer> future2 = op2.execute();
		
		future2.addListener(new Runnable() {

			@Override
			public void run() {
				try {
					value2 = future2.get();
					System.out.println(value2);
					barrier.await();
					
					System.out.println("" + (value1 + value2));
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}, Executors.newCachedThreadPool());
		
	}
	
	public static class Operation implements Callable<Integer>{

		@Override
		public Integer call() throws Exception {
			Random random = new Random(System.nanoTime());
			TimeUnit.SECONDS.sleep(random.nextInt(30)); // waits random time up to a minute
			return Math.abs(random.nextInt(50));
		}
		
	}

}

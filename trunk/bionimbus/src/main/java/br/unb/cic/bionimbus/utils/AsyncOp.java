package br.unb.cic.bionimbus.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**

  AsyncOp permite a chamada de operações assíncronas (RPC, I/O, etc), utilizando recursos do Java 1.5
  e da biblioteca Guava da Google. 
  
  Exemplo de uso:
 	<code>
 			ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
		builder.setDaemon(true).setNameFormat("async");
		
		ExecutorService executor = Executors.newCachedThreadPool(builder.build());
				
		AsyncOp<String> a = new AsyncOp<String>(new Callable<String>() {

			@Override
			public String call() throws Exception {
				TimeUnit.SECONDS.sleep(1);
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
	</code>
 * 
 * @author edward
 *
 * @param <T>
 */
public class AsyncOp<T> {

	protected volatile T result;

	private volatile AsyncOpStatus status = AsyncOpStatus.UNSTARTED;

	private long initTime;
	private long endTime;

	private static final ThreadFactory factory = Executors.defaultThreadFactory();
	private final ExecutorService executor;
	private final long timeout;
	private final Callable<T> operation;

	private Future<T> operationFuture;

	public AsyncOp(Callable<T> operation, ExecutorService executor, long timeout) {
		this.operation = operation;
		this.executor = executor;
		this.timeout = timeout;
	}

	public final T getResult() {
		return result;
	}

	public AsyncOpStatus getStatus() {
		return status;
	}

	public final void cancel() {
		operationFuture.cancel(true);
	}

	public long elapsedTimeInMilliseconds() {
		if (endTime < initTime)
			System.out.println("true");
		return (endTime - initTime + 1);
	}

	public final ListenableFuture<T> execute() {

		if (status != AsyncOpStatus.UNSTARTED)
			throw new IllegalStateException(
					"cannot start async op, current status=" + status);

		final SettableFuture<T> future = SettableFuture.create();

		operationFuture = executor.submit(operation);

		initTime = System.currentTimeMillis();

		status = AsyncOpStatus.EXECUTING;

		Thread resultThread = factory.newThread(new Runnable() {
			public void run() {
				try {

					result = operationFuture.get(timeout, TimeUnit.MILLISECONDS);
					System.out.println("completed!");
					future.set(result);

					endTime = System.currentTimeMillis();

					status = AsyncOpStatus.FINISHED;

				} catch (InterruptedException e) {
					System.out.println("interrupted exception!");
					status = AsyncOpStatus.ABORTED;
					future.setException(e);

				} catch (ExecutionException e) {
					System.out.println("execution exception!");
					status = AsyncOpStatus.ABORTED;
					future.setException(e);

				} catch (TimeoutException e) {
					System.out.println("timeout!");

					status = AsyncOpStatus.TIMED_OUT;
					endTime = System.currentTimeMillis();
					future.setException(e);

				}
			}
		});

		resultThread.start();

		return future;
	}
}

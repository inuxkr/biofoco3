package br.unb.cic.bionimbus.p2p.rpc;

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

public class AsyncOp<T> {

	protected volatile T result;

	private volatile AsyncOpStatus status = AsyncOpStatus.UNSTARTED;

	private long initTime;
	private long endTime;

	private static final ThreadFactory factory = Executors
			.defaultThreadFactory();
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

		Thread execThread = factory.newThread(new Runnable() {
			public void run() {
				try {

					result = operationFuture
							.get(timeout, TimeUnit.MILLISECONDS);
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

		execThread.start();

		return future;
	}
}

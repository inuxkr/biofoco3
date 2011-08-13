package br.unb.cic.bionimbus.p2p.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class AsyncOp<T> implements Runnable {

	protected volatile T result;

	private final Thread timerThread;
	private volatile boolean started = false;
	private final CopyOnWriteArraySet<AsyncOpListener<T>> listeners = new CopyOnWriteArraySet<AsyncOpListener<T>>();
	private final long timeout;
	
	private final Callable<T> operation;

	private long initTime;
	private long endTime;
	
	private static final ThreadFactory factory = Executors.defaultThreadFactory();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Future<T> future;

	public AsyncOp(Callable<T> operation, AsyncOpListener<T> listener, long timeout) {
		this.operation = operation;
//		this.executor = executor;
		this.listeners.add(listener);
		this.timeout = timeout;
		this.timerThread = factory.newThread(this);
	}
	
	public final void completed(T value) {

		this.result = value;

		if (result != null) {
			timerThread.interrupt();
		}
	}

	public final T getResult() {
		return result;
	}

	public final void cancel() {
		if (timerThread.isAlive() && started)
			timerThread.interrupt();
	}

	public final void start() {
		started = true;
		timerThread.start();
		initTime = System.currentTimeMillis();
		future = executor.submit(operation);
	}

	public final void run() {

		if (timeout > 0) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// Thread was interrupted, operation may have completed
			}
		}

		endTime = System.currentTimeMillis();

		for (AsyncOpListener<T> listener : listeners) {
			if (result == null) {
				if ((endTime - initTime) >= timeout) {
					listener.onTimeoutException();
				} else {
					listener.onInterruptedException();
				}
			} else {
				listener.onFinished(result);
			}
		}
	}
	
	public final void addListener(AsyncOpListener<T> listener) {
		listeners.add(listener);
	}

	public boolean removeListener(AsyncOpListener<T> listener) {
		return listeners.remove(listener);
	}
}

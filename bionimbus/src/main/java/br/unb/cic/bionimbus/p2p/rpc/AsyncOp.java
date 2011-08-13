package br.unb.cic.bionimbus.p2p.rpc;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncOp<T> implements Runnable {

	protected volatile T result;

	private final Thread thread;
	private final CopyOnWriteArraySet<AsyncOpListener<T>> listeners = new CopyOnWriteArraySet<AsyncOpListener<T>>();
	private final long timeout;

	private volatile boolean started = false;

	private long initTime;
	private long endTime;
	private static final ThreadFactory factory = Executors.defaultThreadFactory();

	public AsyncOp(AsyncOpListener<T> listener, long timeout) {
		this.listeners.add(listener);
		this.timeout = timeout;
		this.thread = factory.newThread(this);
	}
	
	public final void addListener(AsyncOpListener<T> listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(AsyncOpListener<T> listener) {
		return listeners.remove(listener);
	}

	public final void completed(T value) {

		this.result = value;

		if (result != null) {
			thread.interrupt();
		}
	}

	public final T getResult() {
		return result;
	}

	public final void cancel() {
		if (thread.isAlive() && started)
			thread.interrupt();
	}

	public final void start() {
		thread.start();
		initTime = System.currentTimeMillis();
		started = true;		
	}

	public final void run() {

		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// Thread was interrupted, operation may have completed
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
}

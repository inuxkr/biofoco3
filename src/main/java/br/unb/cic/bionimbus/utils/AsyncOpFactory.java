package br.unb.cic.bionimbus.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public final class AsyncOpFactory {
	
	private final ExecutorService executor;
	private final long defaultTimeout;
	
	public AsyncOpFactory(ExecutorService executor, long timeout) {
		this.executor = executor;
		this.defaultTimeout = timeout;
	}

	public <T> AsyncOp<T> create(Callable<T> operation) {
		return new AsyncOp<T>(operation, executor, defaultTimeout);
	}
	
	public <T> AsyncOp<T> create(Callable<T> operation, long timeout) {
		return new AsyncOp<T>(operation, executor, timeout);
	}	
}

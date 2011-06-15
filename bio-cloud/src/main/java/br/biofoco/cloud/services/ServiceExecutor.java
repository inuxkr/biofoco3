package br.biofoco.cloud.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceExecutor {
 
	private final BlockingQueue<Future<TaskResult>> completed = new LinkedBlockingQueue<Future<TaskResult>>();
	private final ExecutorService executor = Executors.newFixedThreadPool(2);			
	private final CompletionService<TaskResult> taskExecutor = new ExecutorCompletionService<TaskResult>(executor, completed);
		
	public void submitTask(Task task) {
		taskExecutor.submit(new TaskExecutor(task));
	}
	
	public void stop() {
		executor.shutdownNow();
	}
	
	public class TaskExecutor implements Callable<TaskResult> {
		
		private final Task task;
		
		public TaskExecutor(Task task){
			this.task = task;
		}

		@Override
		public TaskResult call() throws Exception {
			return task.execute();
		}		
	}
}

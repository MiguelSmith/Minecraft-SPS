package org.koekepan.herobrine.log.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;

import org.koekepan.herobrine.log.Log;
import org.koekepan.herobrine.log.LogHandler;
import org.koekepan.herobrine.log.Logger;

public class FileLogger extends LogFileWriter implements Logger {

	private BlockingQueue<Log> logs = new LinkedBlockingQueue<Log>();

	private Future<?> future;
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	public FileLogger(LogHandler logHandler, String filePath) throws FileNotFoundException {
		
		super(logHandler, filePath);
		
		future = executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					while(!Thread.currentThread().isInterrupted()) {
//						System.out.println("FileLogger::run => Attempting to consume log from 'logs'");						
						Log log = logs.take();
//						System.out.println("FileLogger::run => Consumed a "+log.getClass().getSimpleName()+" log. 'logs' queue now has size "+logs.size());								
						write(log);
					}
				} catch(InterruptedException e) {
					System.out.println("FileLogger::run => Caught InterruptedException. 'logs' queue now has size "+logs.size());	
					Thread.currentThread().interrupt();
					e.printStackTrace();

				}  catch(IOException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void log(Log log) {
		try {
	//		System.out.println("FileLogger::log => Putting log "+log.getClass().getSimpleName()+" on 'logs' queue");
			logs.put(log);
	//		System.out.println("FileLogger::log => 'logs' queue now has size +"+logs.size());

		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		
		try {
			while(logs.size() > 0) {
				Thread.sleep(1);
			}
			
			future.cancel(true);
			while(!future.isDone()) {
				Thread.sleep(1);
			}

			super.close();
			
			executorService.shutdown();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} catch(IOException e) {
			// do nothing
			e.printStackTrace();
		}
	}

}

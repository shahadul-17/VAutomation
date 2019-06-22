package com.bjitgroup.vautomation.core.services;

import java.util.LinkedList;
import java.util.Queue;

import com.bjitgroup.vautomation.Application;
import com.bjitgroup.vautomation.core.models.BackgroundTask;

public class BackgroundWorker extends Thread {
	
	private Queue<BackgroundTask> backgroundTaskQueue;
	
	public BackgroundWorker() {
		backgroundTaskQueue = new LinkedList<BackgroundTask>();
	}
	
	public void add(BackgroundTask backgroundTask) {
		if (backgroundTask == null) {
			return;
		}
		
		backgroundTaskQueue.add(backgroundTask);
	}
	
	@Override
	public void run() {
		while (Application.isRunning) {
			if (backgroundTaskQueue.isEmpty()) {
				try {
					Thread.sleep(1000);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				
				continue;
			}
			
			backgroundTaskQueue.remove().execute();
		}
	}
	
}
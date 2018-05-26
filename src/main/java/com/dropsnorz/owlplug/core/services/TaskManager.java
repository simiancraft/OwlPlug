package com.dropsnorz.owlplug.core.services;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.dropsnorz.owlplug.core.controllers.TaskBarController;

import javafx.application.Platform;
import javafx.concurrent.Task;


@Service
public class TaskManager {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	TaskBarController taskBarController;
	
	AsyncListenableTaskExecutor  exec = new SimpleAsyncTaskExecutor();
	    
    private ArrayList<Task> pendingTasks = new ArrayList<Task>();
    private Task currentTask= null;
    
    TaskManager(){

    }
    
    public void addTask(Task task) {
    	
    	log.debug("Task submited to queue - " + task.getClass().getCanonicalName());
    	pendingTasks.add(task);
    	refresh(false);
    	
    }
    
    private synchronized void refresh(boolean deleteCurrentTask) {
    	    	
    	if(deleteCurrentTask) {
    		log.debug("Remove task from queue -  " + currentTask.getClass().getCanonicalName());
    		pendingTasks.remove(currentTask);
    		currentTask = null;
    		taskBarController.taskProgressBar.progressProperty().unbind();
    		taskBarController.taskLabel.textProperty().unbind();
    	}

    	if(pendingTasks.size() > 0 && currentTask == null) {
    		this.currentTask = pendingTasks.get(0);
    		
    		log.debug("Task submitted to executor - " + currentTask.getClass().getCanonicalName());
    		ListenableFuture future = exec.submitListenable(currentTask);
    		taskBarController.taskProgressBar.progressProperty().bind(currentTask.progressProperty());
    		taskBarController.taskLabel.textProperty().bind(currentTask.messageProperty());
    		future.addCallback(new ListenableFutureCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					// TODO Auto-generated method stub
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							refresh(true);
							
						}
						
					});

				}

				@Override
				public void onFailure(Throwable ex) {
					
					refresh(true);
					
				}
    		
    		});
    		
    	}
    }
    
    
	

}

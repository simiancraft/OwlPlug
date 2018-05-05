package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.controllers.PluginsController;
import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.engine.tasks.DirectoryRemoveTask;
import com.dropsnorz.owlplug.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;
import com.dropsnorz.owlplug.model.PluginRepository;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

@Service
public class TaskFactory {

	@Autowired
	PluginService pluginService;
	@Autowired
	TaskManager taskManager;
	@Autowired
	PluginDAO pluginDAO;
	@Autowired 
	PluginRepositoryDAO pluginRepositoryDAO;



	@Autowired
	PluginsController pluginsController;

	public void run(Task task) {
		taskManager.addTask(task);
	}

	public SyncPluginTask createSyncPluginTask() {

		SyncPluginTask task = new SyncPluginTask(pluginService, pluginDAO);

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {

				pluginsController.refreshPlugins();

			}
		});
		return task;
	}

	public PluginRemoveTask createPluginRemoveTask(Plugin plugin) {

		PluginRemoveTask task = new PluginRemoveTask(plugin, pluginDAO);

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {

				pluginsController.refreshPlugins();

			}
		});

		return task;
	}

	public DirectoryRemoveTask createDirectoryRemoveTask(PluginDirectory pluginDirectory) {

		DirectoryRemoveTask task = new DirectoryRemoveTask(pluginDirectory);

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {

				taskManager.addTask(createSyncPluginTask());

			}
		});

		return task;
	}

	public RepositoryRemoveTask createRepositoryRemoveTask(PluginRepository repository, String path) {

		RepositoryRemoveTask task = new RepositoryRemoveTask(pluginRepositoryDAO, repository, path);
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {

				taskManager.addTask(createSyncPluginTask());

			}
		});
		return task;


	}


}

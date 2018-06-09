package com.dropsnorz.owlplug.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.core.controllers.PluginsController;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.core.engine.tasks.DirectoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.core.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.model.PluginRepository;

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
	protected RepositoryStrategyResolver repositoryStrategyResolver;



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


	public RepositoryTask createRepositoryTask(PluginRepository repository, RepositoryStrategyParameters parameters) {

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);

		RepositoryTask task = new RepositoryTask() {
			@Override
			protected Void call() throws Exception {

				this.updateProgress(0, 1);
				try {
					strategy.execute(repository, parameters);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.updateProgress(1, 1);

				return null;
			}
		};

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {

				pluginsController.refreshPlugins();

			}
		});
		
		return task;

	}

}

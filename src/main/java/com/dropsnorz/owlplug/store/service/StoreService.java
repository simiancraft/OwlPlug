package com.dropsnorz.owlplug.store.service;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.tasks.TaskException;
import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.json.PluginStoreJsonMapper;
import com.dropsnorz.owlplug.store.model.json.StoreModelAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import java.io.File;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private TaskFactory taskFactory;
	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	@Autowired
	private StoreProductDAO storeProductDAO;


	@PostConstruct
	private void init() {

		PluginStore store = pluginStoreDAO.findByName("OwlPlug Central");

		if (store == null) {
			store = new PluginStore();
			store.setName("OwlPlug Central");
		}	

		store.setUrl("http://owlplug.dropsnorz.com/store.json");
		pluginStoreDAO.save(store);


	}

	public void syncStores() {
		taskFactory.createStoreSyncTask().run();
	}


	/**
	 * Retrieves all products from stores which are compatible with the current platform.
	 * @return list of store products
	 */
	public Iterable<StoreProduct> getStoreProducts() {
		OSType osType = applicationDefaults.getPlatform();
		String platformTag = osType.getCode();

		ArrayList<StoreProduct> result = new ArrayList<>();
		Iterables.addAll(result, storeProductDAO.findByPlatform(platformTag));
		Iterables.addAll(result, storeProductDAO.findProductWithoutPlatformAssignment(""));
		return result;
	}

	/**
	 * Retrieves products from store with name matching the given parameters and 
	 * compatible with the current platform.
	 * @param name part of the plugin name
	 * @return
	 */
	public Iterable<StoreProduct> getStoreProducts(String name) {
		OSType osType = applicationDefaults.getPlatform();
		String platformTag = osType.getCode();

		ArrayList<StoreProduct> result = new ArrayList<>();
		Iterables.addAll(result, storeProductDAO.findByPlatformAndName(platformTag,name));
		Iterables.addAll(result, storeProductDAO.findProductWithoutPlatformAssignment(name));
		return result;
	}

	public Iterable<StoreProduct> getProductsByName(String name) {
		return storeProductDAO.findByNameContainingIgnoreCase(name);
	}

	public void install(StoreProduct product, File targetDirectory) {
		taskFactory.createProductInstallTask(product, targetDirectory).run();
	}

	/**
	 * Creates a PluginStore instance requesting a store url endpoint.
	 * @param url Store endpoint url
	 * @return created pluginstore instance, null if an error occurs
	 */
	public PluginStore getPluginStoreFromUrl(String url) {

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			ObjectMapper objectMapper = new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				PluginStoreJsonMapper pluginStoreTO = objectMapper.readValue(entity.getContent(), PluginStoreJsonMapper.class);
				EntityUtils.consume(entity);
				return StoreModelAdapter.jsonMapperToEntity(pluginStoreTO);

			} catch (Exception e) {
				log.error("Error parsing store response: " + url, e);
				throw new TaskException(e);
			} finally {
				response.close();
			}

		} catch (Exception e) {
			return null;

		}

	}

}

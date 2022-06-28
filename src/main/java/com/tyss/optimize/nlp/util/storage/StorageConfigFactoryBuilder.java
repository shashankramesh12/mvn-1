package com.tyss.optimize.nlp.util.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class StorageConfigFactoryBuilder {

	private static Map<String, StorageManager> storageCache;

	@Autowired
	public StorageConfigFactoryBuilder(ApplicationContext context) {

		storageCache = context.getBeansOfType(StorageManager.class);
	}

	public static StorageManager getStorageManager(String type) {

		if(type.equals("cloudS3")){
			type = "CloudS3";
		}
		if (null != storageCache && storageCache.containsKey(type) ) {

			return storageCache.get(type);
		}

		return storageCache.get("SharedDrive");
	}


}

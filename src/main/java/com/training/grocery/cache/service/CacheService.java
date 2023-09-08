package com.training.grocery.cache.service;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import com.training.grocery.product.dbmodel.Product;
import com.training.grocery.product.hibernate.ProductService;

@Service
public class CacheService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	CacheManager cacheManager;
	@Autowired
	ProductService productservice;


	@PostConstruct
	public void init() {
		cacheManager.getCache("products").clear();

		for (Product product : productservice.getAllProducts()) {
			cacheManager.getCache("products").put(product.getId(), product);
		}
		logger.info("Products cache generated");
	}


	public void update(Product product) {
		cacheManager.getCache("products").evictIfPresent(product.getId());
		cacheManager.getCache("products").put(product.getId(), product);
		logger.info("Cache updated for product" + product.getId());
	}

	public void delete(Product product) {

		cacheManager.getCache("products").evictIfPresent(product.getId());
		logger.info("Cache deleted for product" + product.getId());
	}

	public void refreshCache() {
		cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
		init();
		logger.info("Refreshed Cache");
	}
}

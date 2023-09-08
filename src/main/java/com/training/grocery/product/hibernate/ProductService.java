package com.training.grocery.product.hibernate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.gson.Gson;
import com.training.grocery.cache.service.CacheService;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.genericsearch.GenericSpecificationBuilder;
import com.training.grocery.genericsearch.SortType;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.product.datamodel.DBProduct;
import com.training.grocery.product.dbmodel.Product;
import com.training.grocery.product.searchReq.ProductReq;
import com.training.grocery.user.datamodel.GsonUtil;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.UserService;

@Service
public class ProductService {

	GenericFilter genericFilter = new GenericFilter();

	@Autowired
	UserService userService;

	@Autowired
	ProductRepository repository;


	@Autowired
	CacheService cacheservice;


	@Autowired
	private com.training.grocery.genericsearch.SpecificationFactory<DBProduct> DBproductSpecificationFactory;

	Logger log = LoggerFactory.getLogger(this.getClass());

	public Product findProdById(String id) {
		DBProduct dbProduct = repository.findByProductId(id);
		if (dbProduct == null)
			return null;
		return serialise(dbProduct);
	}

	@Cacheable("products")
	public List<Product> getAllProducts() {
		List<DBProduct> allProducts = repository.findAll();
		List<DBProduct> allProductstemp = repository.findAll();
		for (DBProduct prod : allProductstemp) {
			if (prod.isDeleted()) {
				allProducts.remove(prod);
			}
		}
		List<Product> res = new ArrayList<Product>();
		for (DBProduct dbProduct : allProducts) {
			res.add(serialise(dbProduct));
		}
		return res;
	}

	public List<Product> getAllByName(String productName) {
		List<DBProduct> allProducts = repository.findByName(productName);
		List<Product> res = new ArrayList<Product>();
		for (DBProduct prod : allProducts) {
			if (prod.isDeleted) {
				allProducts.remove(prod);
			} else {
				res.add(serialise(prod));
			}
		}
		return res;
	}

	public List<Product> getAllByNameCategory(String productName, String category) {
		List<DBProduct> allProducts = repository.findByNameAndCategory(productName, category);
		List<Product> res = new ArrayList<Product>();
		for (DBProduct prod : allProducts) {
			if (prod.isDeleted) {
				allProducts.remove(prod);
			} else {
				res.add(serialise(prod));
			}
		}
		return res;
	}

	public void create(Product product) {
		log.warn("threadlocal might not have the userid", genericFilter.threadLocal.get());
		Long uid = Long.parseLong(GenericFilter.threadLocal.get());
		User user = userService.findById(uid);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		product.setUserid(userid);
		product.setTime(LocalDateTime.now());
		cacheservice.update(product);
		repository.save(deserialise(product));
	}

	public void update(Product updatedProduct) {
		Product productToUpdate = serialise(repository.findByProductId(updatedProduct.getProductId()));
		Gson gson = new Gson();
		String prodtostr = gson.toJson(productToUpdate);
		Map toup = gson.fromJson(prodtostr, Map.class);
		String prodfromstr = gson.toJson(updatedProduct);
		Map fromup = gson.fromJson(prodfromstr, Map.class);
		fromup.forEach((k, v) -> toup.put(k, v));
		prodtostr = gson.toJson(toup);
		Product newprod = gson.fromJson(prodtostr, Product.class);
		repository.save(deserialise(newprod));
		cacheservice.update(newprod);
		log.info("Prod details of {} updated successfully", updatedProduct.getProductId());
	}

	public void delete(Long id) {
		DBProduct prod = repository.findById(id).orElse(null);
		prod.setDeleted(true);
		cacheservice.delete(serialise(prod));
		repository.save(prod);
	}

	public Page<DBProduct> productSearch(ProductReq productReq) {
		GenericSpecificationBuilder<DBProduct> builder = new GenericSpecificationBuilder<>();
		if (!StringUtils.isEmpty((productReq.getProductName()))) {
			builder.with(DBproductSpecificationFactory.isIN("name", productReq.getProductName()));
		}
		if (!StringUtils.isEmpty((productReq.getCategory()))) {
			builder.with(DBproductSpecificationFactory.isIN("category", productReq.getCategory()));
		}
		if (!StringUtils.isEmpty(productReq.getUpperTime())) {
			builder.with(DBproductSpecificationFactory.isLessThanEqualTo("creationTime", productReq.getUpperTime()));
		}
		if (!StringUtils.isEmpty(productReq.getLowerTime())) {
			builder.with(DBproductSpecificationFactory.isGreaterThanEqualTo("creationTime", productReq.getLowerTime()));
		}
		Pageable pageable;
		if (!StringUtils.isEmpty(productReq.getSortType())) {
			if (productReq.getSortType().equals(SortType.ASC)) {
				pageable = PageRequest.of(productReq.getPage(), productReq.getSize(),
						Sort.by(productReq.getSortBy()).ascending());
			} else {
				pageable = PageRequest.of(productReq.getPage(), productReq.getSize(),
						Sort.by(productReq.getSortBy()).descending());
			}
		} else {
			pageable = PageRequest.of(productReq.getPage(), productReq.getSize(), Sort.by(productReq.getSortBy()));
		}
		try {
			return repository.findAll(pageable);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}

	}

	public Product serialise(DBProduct dbProd) {
		GsonUtil gsonUtil = new GsonUtil();
		Product prod = (Product) gsonUtil.convert(new Product(), dbProd);
		log.info("Serialised : " + dbProd.getName());
		return prod;
	}

	public DBProduct deserialise(Product prod) {
		GsonUtil gsonUtil = new GsonUtil();
		DBProduct dbProd = (DBProduct) gsonUtil.convert(new DBProduct(), prod);
		String productId = (new StringRandom().generateRandomString(prod.getId(), prod.getName()));
		dbProd.setProductId(productId);
		log.info("Deserialised : " + prod.getName());
		return dbProd;
	}

	public boolean checkIfProductExists(String id) {
		DBProduct dbProd = repository.findByProductId(id);
		if (dbProd == null || dbProd.isDeleted)
			return true;
		return false;
	}

	public Product findByProductId(String id) throws Exception {
		try {
			DBProduct dbProd = repository.findByProductId(id);
			return serialise(dbProd);
		} catch (Exception e) {
			return null;
		}
	}

}

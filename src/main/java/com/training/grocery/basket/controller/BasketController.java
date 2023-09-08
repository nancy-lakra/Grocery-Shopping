package com.training.grocery.basket.controller;

import javax.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.training.grocery.basket.dbmodel.Basket;
import com.training.grocery.basket.hibernate.BasketRepo;
import com.training.grocery.basket.hibernate.BasketService;
import com.training.grocery.basket.requestmodel.BasketReq;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.product.hibernate.ProductService;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.UserService;


@RestController
@RequestMapping(value = "/basket/")
public class BasketController {

	GenericFilter genericFilter = new GenericFilter();

	@Autowired
	BasketService service;

	@Autowired
	BasketRepo basketRepo;

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public Basket getBasket() {
		Long id = Long.parseLong(genericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		logger.info("Get method /basket/show, by userid " + userid);
		return service.show(userid);
	}

	@RequestMapping(value = "/alter", method = RequestMethod.POST)
	public ResponseEntity<String> alterBasket(@RequestBody BasketReq req) {

		Long id = Long.parseLong(genericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		logger.info("Post method /basket/add, by userid " + userid);
		System.out.println("Here1");
		if (req.getProductid() == null || req.getQuantity() == null || req.getQuantity() < 0) {
			logger.error("Invalid Details for /basket/alter " + req);
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
		System.out.println("Here2");
		if (req.getQuantity() == 0) {
			logger.debug("Requested quantiry = 0, Deleting" + req);
			service.delete(userid, req.getProductid());
			return ResponseEntity.ok("SUCCESS");
		}
		System.out.println("Here3");
		if (productService.findProdById(req.getProductid()).isDeleted()) {
			logger.error("Attempt to delete already delete data ");
			throw new EntityExistsException(EnumsForExceptions.Deleted_Product_Exists.toString());
		}
		System.out.println("Here4");
		service.alter(userid, req.getProductid(), req.getQuantity());
		System.out.println("Here5");
		logger.info("Product Altered, Exiting");
		return ResponseEntity.ok("SUCCESS");
	}
}

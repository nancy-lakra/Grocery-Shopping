package com.training.grocery.product.controller;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.gson.Gson;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.product.datamodel.DBProduct;
import com.training.grocery.product.dbmodel.Product;
import com.training.grocery.product.hibernate.ProductRepository;
import com.training.grocery.product.hibernate.ProductService;
import com.training.grocery.product.searchReq.ProductReq;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.DBuserRepository;
import com.training.grocery.user.hibernate.UserService;

@Controller
public class ProductController {

	@Autowired
	ProductService service;

	@Autowired
	DBuserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	ProductRepository productRepository;

	Logger log = LoggerFactory.getLogger(this.getClass());

	GenericFilter genericFilter = new GenericFilter();

	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> findAll() {
		log.info("Fetching list of all products");
		return new ResponseEntity<List<Product>>(service.getAllProducts(), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/search", method = RequestMethod.POST)
	public ResponseEntity<Page<DBProduct>> findAllByName(@RequestBody ProductReq productReq) {
		log.info("Finding products ");
		return new ResponseEntity<Page<DBProduct>>(service.productSearch(productReq), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/add", method = RequestMethod.POST)
	public ResponseEntity<String> addproduct(@RequestBody Product productDetails) {
		log.info("Calling product/add API");
		Long id = Long.parseLong(genericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		productDetails.setUserid(userid);
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		Gson gson = new Gson();
		String prodtostr = gson.toJson(productDetails);
		Map newprod = gson.fromJson(prodtostr, Map.class);
		if (newprod.size() < 8) {
			log.error("Missing Details in JSON Object", productDetails);
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
		if (userid != productDetails.getUserid()) {
			log.error("Userid mismatch. userid in threadlocal={}, userid in request={}", userid,
					productDetails.getUserid());
			throw new AccessDeniedException(EnumsForExceptions.User_Id_mismatch.toString());
		}
		if (productDetails.getPrice() <= 0 || productDetails.getStock() <= 0 || productDetails.getDiscount() < 0) {
			log.error("Invalid value for price/quantity/discount", productDetails);
			throw new HttpMessageNotReadableException(EnumsForExceptions.Invalid_Value_Product.toString());
		}

		if (user.getRole().toString().equals("SL")) {
			if (productRepository.findById(productDetails.getId()).orElse(null) != null) {
				log.warn("Product already exists with this ProductId={}", productDetails.getProductId());
				throw new EntityExistsException(EnumsForExceptions.Product_Found.toString());
			}
			service.create(productDetails);
			log.info("Exiting product/add API");
			return new ResponseEntity<String>("Procuct Added!", HttpStatus.CREATED);
		}
		log.warn("Bad Request, Something went wrong", productDetails);
		throw new HttpMessageNotReadableException(EnumsForExceptions.Invalid_Product_Request.toString());
	}

	@RequestMapping(value = "/products/update/", method = RequestMethod.PUT)
	public ResponseEntity<String> updateProduct(@RequestBody Product productDetails) {
		log.info("Entered products/update API");
		if (productDetails.getProductId() == null) {
			log.error("Missing productId in JSON request", productDetails);
			throw new HttpMessageNotReadableException(EnumsForExceptions.ProductId_null.toString());
		}
		Long id = Long.parseLong(genericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		if (!userid.equals(productDetails.getUserid())) {
			log.error("Userid mismatch. userid in threadlocal={}, userid in request={}", userid,
					productDetails.getUserid());
			throw new AccessDeniedException(EnumsForExceptions.User_Id_mismatch.toString());
		}
		if (!user.getRole().toString().equals("SL")) {
			log.error("User {} is not a seller", user.getId());
			throw new AccessDeniedException(EnumsForExceptions.User_Not_Seller.toString());
		}
		if (service.findProdById(productDetails.getProductId()) == null) {
			log.error("No such product in the database", productDetails);
			throw new EntityNotFoundException(EnumsForExceptions.Product_Not_Found.toString());
		}
		if (service.findProdById(productDetails.getProductId()).isDeleted()) {
			log.debug("Already deleted product was tried to be updated ...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		service.update(productDetails);
		log.info("Exiting products/update");
		return new ResponseEntity<String>("Product Details Updated Successfully ", HttpStatus.OK);
	}

	@RequestMapping(value = "/products/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") Long productId) {
		log.info("Entered products/delete");
		Long id = Long.parseLong(genericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		if (!user.getRole().toString().equals("SL")) {
			log.error("User is not seller. The user details are: ", user);
			throw new AccessDeniedException(EnumsForExceptions.User_Not_Seller.toString());
		}
		if (productRepository.findById(productId).orElse(null) == null) {
			log.error("No such product in the databasew with ID:", productId);
			throw new EntityNotFoundException(EnumsForExceptions.Product_Not_Found.toString());
		}
		if (!userid.equals(productRepository.findById(productId).get().getUserid())) {
			log.error("Userid mismatch. userid in threadlocal={}, userid in request={}", userid,
					productRepository.findById(productId).get().getUserid());
			throw new AccessDeniedException(EnumsForExceptions.User_Id_mismatch.toString());
		}
		service.delete(productId);
		log.info("Exiting products/delete");
		return new ResponseEntity<String>("Product Deleted Successfully!", HttpStatus.OK);
	}
}

package com.training.grocery.order.controller;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.training.grocery.basket.hibernate.BasketRepo;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.order.datamodel.Status;
import com.training.grocery.order.dbmodel.Order;
import com.training.grocery.order.hibernate.OrderService;
import com.training.grocery.order.requestmodel.ObjHolder;
import com.training.grocery.order.requestmodel.OrderHolder;
import com.training.grocery.order.searchReq.OrderReq;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.UserService;


@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	@Autowired
	OrderService orderService;

	@Autowired
	UserService userService;

	@Autowired
	BasketRepo basketRepo;

	Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public ResponseEntity<String> createOrder(@RequestBody OrderHolder order) throws Exception {
		Long id = Long.parseLong(GenericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		log.info("/orders/place by userid : " + userid);

		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		if (!String.valueOf(order.getBuyer_id()).equals(userid))
			throw new AccessDeniedException(EnumsForExceptions.Not_Authorized.toString());
		if (basketRepo.findByUserid(userid).getProducts().isEmpty())
			throw new EntityNotFoundException(EnumsForExceptions.Basket_Empty.toString());
		if (orderService.isInRecords(order.getOrder_id()))
			throw new EntityExistsException(EnumsForExceptions.Order_Exists.toString());
		orderService.createOrder(order);
		return new ResponseEntity<String>("Order Placed !!", HttpStatus.CREATED);
	}

	@RequestMapping(value = "/showAll", method = RequestMethod.GET)
	public ResponseEntity<?> showOrders(@RequestBody OrderReq orderReq) throws Exception {
		Long id = Long.parseLong(GenericFilter.threadLocal.get());
		User user = userService.findById(id);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		log.info("/orders/showAll by userid : " + userid);
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		Page<Order> ol = orderService.searchOrder(userid, orderReq);
		return new ResponseEntity<Page<Order>>(ol, HttpStatus.OK);
	}

	@RequestMapping(value = "/showStatus/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> showStatus(@PathVariable Long id) throws Exception {
		if (orderService.isInRecords(id) == false)
			throw new EntityNotFoundException(EnumsForExceptions.Order_Not_In_Records.toString());
		Long uid = Long.parseLong(GenericFilter.threadLocal.get());
		User user = userService.findById(uid);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		if (!String.valueOf(orderService.findById(id).getBuyer_id()).equals(userid))
			throw new AccessDeniedException(EnumsForExceptions.Not_Authorized.toString());

		String stat = orderService.showStatus(id);
		return new ResponseEntity<String>(stat, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateStatus", method = RequestMethod.PUT)
	public ResponseEntity<String> updateStatus(@RequestBody ObjHolder holder) throws Exception {
		Long uid = Long.parseLong(GenericFilter.threadLocal.get());
		User user = userService.findById(uid);
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(userid)) {
			log.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		if (!String.valueOf(orderService.findById(holder.getId()).getBuyer_id()).equals(userid))
			throw new AccessDeniedException(EnumsForExceptions.User_Id_mismatch.toString());
		if (orderService.isInRecords(holder.getId()) == false)
			throw new EntityNotFoundException(EnumsForExceptions.Order_Not_In_Records.toString());
		String ret = orderService.updateOrder(holder.getId(), holder.getStatus());
		Status st = holder.getStatus();
		if (st == Status.RETURN_REQUESTED) {
			if (ret.equals(""))
				return new ResponseEntity<String>(
						"Order Cancelled...Please wait for refund and recollection of grocery...", HttpStatus.OK);
		}
		if (st == Status.CANCEL_REQUEST) {
			if (ret.equals(""))
				return new ResponseEntity<String>("Order Cancelled ...", HttpStatus.OK);
		}
		return null;
	}

}

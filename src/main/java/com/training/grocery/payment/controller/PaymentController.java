package com.training.grocery.payment.controller;

import javax.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.payment.dbmodel.Payment;
import com.training.grocery.payment.hibernate.PaymentService;
import com.training.grocery.payment.requestmodel.PaymentRequest;
import com.training.grocery.payment.searchReq.PaymentReq;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.UserService;

@RestController
@RequestMapping(value = "/payment/")
public class PaymentController {

	@Autowired
	PaymentService service;

	@Autowired
	UserService userService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> addMoney(@RequestBody PaymentRequest req) {

		String id = GenericFilter.threadLocal.get();
		User user = userService.findById(Long.parseLong(id));
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		logger.info("Get method /add/" + req.getAmount() + " , by userid " + userid);


		if (userService.checkIfUserExists(userid)) {
			logger.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}


		if (req.getAmount() <= 0 || req.getAmount() > 10000)
			throw new HttpMessageNotReadableException(EnumsForExceptions.Invalid_Amount.toString());

		service.transfer("Bank", userid, -1, req.getAmount(), req.getComment(), "T");
		return ResponseEntity.ok("SUCCESS");
	}

	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
	public ResponseEntity<String> withdrawMoney(@RequestBody PaymentRequest req) {

		String id = GenericFilter.threadLocal.get();
		User user = userService.findById(Long.parseLong(id));
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		logger.info("Post method /withdraw/" + req.getAmount() + " , by userid " + userid);


		if (userService.checkIfUserExists(userid)) {
			logger.debug("Already deleted user tried to access APIs...");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}

		if (req.getAmount() <= 0 || req.getAmount() > 10000)
			throw new HttpMessageNotReadableException(EnumsForExceptions.Invalid_Amount.toString());

		service.transfer(userid, "Bank", -1, req.getAmount(), req.getComment(), "W");
		return ResponseEntity.ok("SUCCESS");
	}

	@RequestMapping(value = "/showpayments", method = RequestMethod.POST)
	public Page<Payment> showPayments(@RequestBody PaymentReq paymentReq) {
		String userid = GenericFilter.threadLocal.get();
		Page<Payment> payments = service.searchPayments(userid, paymentReq);
		return payments;
	}
}

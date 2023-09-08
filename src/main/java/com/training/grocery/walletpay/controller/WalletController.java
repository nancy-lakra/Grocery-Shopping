package com.training.grocery.walletpay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.UserService;
import com.training.grocery.walletpay.hibernate.WalletService;


@RestController
@RequestMapping(value = "/payment/")
public class WalletController {


	@Autowired
	WalletService service;

	@Autowired
	UserService userService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/amount", method = RequestMethod.GET)
	public ResponseEntity<String> getAmount() {
		String id = GenericFilter.threadLocal.get();
		User user = userService.findById(Long.parseLong(id));
		String userid = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		logger.info("Get method /amount, by userid " + userid);
		if (userService.checkIfUserExists(userid)) {
			logger.debug("Already deleted user tried to access their wallet...");
			return new ResponseEntity<String>("Already existing inactive user...signup with diff details",
					HttpStatus.NOT_ACCEPTABLE);
		}
		String amt = String.valueOf(service.getAmount(userid));
		return new ResponseEntity<String>("The wallet balance is : " + amt, HttpStatus.OK);
	}
}

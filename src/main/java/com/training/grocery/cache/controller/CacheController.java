package com.training.grocery.cache.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.training.grocery.cache.service.CacheService;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.user.datamodel.DBUser;
import com.training.grocery.user.hibernate.DBuserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

	@Autowired
	CacheService cachingService;
	@Autowired
	DBuserRepository userRepository;
	Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/refreshCache", method = RequestMethod.GET)
	public ResponseEntity<String> clearAllCaches() {
		Long userid = Long.parseLong(GenericFilter.threadLocal.get());
		log.info("/refreshCache by userid : " + Long.toString(userid));

		DBUser user = userRepository.findById(userid).get();
		if (!user.getRole().equals("ADMIN")) {
			log.error("User {} is not a Admin ", userid);
			throw new AccessDeniedException(EnumsForExceptions.User_Not_Admin.toString());
		}
		cachingService.refreshCache();
		return new ResponseEntity<String>("Cache cleared!", HttpStatus.OK);

	}
}

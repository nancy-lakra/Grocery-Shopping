package com.training.grocery.jwt.controllers;

import javax.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.requestmodel.JwtRequest;
import com.training.grocery.jwt.responsemodel.JwtResponse;
import com.training.grocery.jwt.service.CustomUserDetailsService;
import com.training.grocery.jwt.utils.JwtUtil;
import com.training.grocery.user.hibernate.UserService;


@RestController
public class JwtController {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@RequestMapping(value = "/users/login", method = RequestMethod.POST)
	public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
		log.info("Entered users/login, Requesting token");
		log.debug("Requesting token for user {}", jwtRequest.getUsername());
		try {
			String userid = userService.findByUserName(jwtRequest.getUsername());
			if (userid != null && userService.checkIfUserExists(userid)) {
				log.debug("Already deleted user tried to access their account ...");
				throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
			}
			this.authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
			log.debug("User {} Authenticated", jwtRequest.getUsername());
		} catch (Exception e) {
			log.error("Bad Credentials", e);
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Credentials.toString());
		}
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
		String token = this.jwtUtil.generateToken(userDetails);
		log.info("Token Gnerated ");
		return ResponseEntity.ok(new JwtResponse(token));
	}
}

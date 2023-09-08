package com.training.grocery.user.controller;

import java.util.Map;
import javax.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.user.datamodel.DBUser;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.hibernate.DBuserRepository;
import com.training.grocery.user.hibernate.UserService;
import com.training.grocery.user.searchReq.UserReq;


@RestController
@RequestMapping(value = "/users")

public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	DBuserRepository userRepo;

	@Autowired
	GenericFilter genericFilter;

	Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ResponseEntity<String> signUp(@RequestBody User user) throws Exception {
		log.info("Entered users/signUp");
		Gson gson = new Gson();
		String usertostr = gson.toJson(user);
		Map newuser = gson.fromJson(usertostr, Map.class);
		if (newuser.size() < 7) {
			log.error("Missing data in user JSON request: {}", user);
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
		if (userService.isInRecords(user) == false) {
			System.out.println("FYSGFUSFGUFHUOHFUOFHUFGOUIHWIDHSIHDISDHSIDH\n\n\n\n\n\n\n\n");
			userService.create(user);
			log.info("New user created with id = {} and role = {}", user.getEmail(), user.getRole().toString());
			return new ResponseEntity<String>("User created !!", HttpStatus.CREATED);
		}

		String id = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(id)) {
			log.error("Already deleted user tried to signup");
			throw new EntityExistsException(EnumsForExceptions.Deleted_User_Exists.toString());
		}
		throw new EntityExistsException(EnumsForExceptions.User_Exists.toString());
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.info("Entered users/update");
		if (user.getId() == null) {
			log.error("The Id is missing in JSON request for users/update", user);
			return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
		}
		if (user.getId() != Long.parseLong(genericFilter.threadLocal.get())) {
			log.error("UserId in threadlocal doesn't natch with the userId in JSON request: {}", user);
			return new ResponseEntity<String>("You cannot update detials of this user", HttpStatus.FORBIDDEN);
		}
		String id = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		if (userService.checkIfUserExists(id)) {
			log.debug("Already deleted user tried to update details...");
			return new ResponseEntity<String>("Already existing inactive user...signup with diff details",
					HttpStatus.NOT_ACCEPTABLE);
		}
		boolean updated = userService.update(user);
		if (updated) {
			log.info("User details of  {} successfully updated", user.getId());
			return new ResponseEntity<String>("User updated !!", HttpStatus.OK);
		}
		log.info("User {} doesn't exist", user.getId());
		log.info("Exiting user/update");
		return new ResponseEntity<String>("User not found ...", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/deleteAccount", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteAccount(@RequestBody Long userid) {
		if (userid == null)
			return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
		if (userid != Long.parseLong(genericFilter.threadLocal.get()))
			return new ResponseEntity<String>("You cannot delete this user", HttpStatus.FORBIDDEN);
		String id = userRepo.findByEmail(String.valueOf(userid)).getUserid();
		if (userService.checkIfUserExists(id)) {
			log.debug("Already deleted user tried to update details...");
			return new ResponseEntity<String>("Already existing inactive user...signup with diff details",
					HttpStatus.NOT_ACCEPTABLE);
		}
		userService.deleteUser(userService.findById(userid));
		return new ResponseEntity<String>("User deleted", HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ResponseEntity<Page<DBUser>> searchUsers(@RequestBody UserReq userReq) {
		String userid = GenericFilter.threadLocal.get();
		if (!userService.findById(Long.parseLong(userid)).getRole().toString().equals("AD"))
			throw new AccessDeniedException(EnumsForExceptions.Not_Authorized.toString());

		return new ResponseEntity<Page<DBUser>>(userService.searchUsers(userReq), HttpStatus.OK);
	}

	/// remaining part -
	// sign out

}

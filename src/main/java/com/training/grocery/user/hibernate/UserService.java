package com.training.grocery.user.hibernate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.gson.Gson;
import com.training.grocery.basket.hibernate.BasketService;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.genericsearch.GenericSpecificationBuilder;
import com.training.grocery.genericsearch.SortType;
import com.training.grocery.genericsearch.SpecificationFactory;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.user.datamodel.DBUser;
import com.training.grocery.user.datamodel.GsonUtil;
import com.training.grocery.user.datamodel.StringRandom;
import com.training.grocery.user.dbmodel.User;
import com.training.grocery.user.searchReq.UserReq;
import com.training.grocery.walletpay.hibernate.WalletService;

@Service
public class UserService {

	@Autowired
	DBuserRepository dbuserRepository;

	@Autowired
	WalletService walletService;

	@Autowired
	BasketService basketService;

	@Autowired
	GenericFilter genericFilter;

	@Autowired
	private SpecificationFactory<DBUser> DBuserSpecificationFactory;

	Logger log = LoggerFactory.getLogger(this.getClass());


	public User findById(Long id) {
		DBUser user = dbuserRepository.findById(id).orElse(null);

		if (user == null)
			return null;

		return serialise(user);
	}

	public List<User> getAll() {
		List<DBUser> list = dbuserRepository.findAll();
		for (DBUser user : list) {
			if (user.isDeleted) {
				list.remove(user);
			}
		}
		List<User> res = new ArrayList<User>();
		for (DBUser user : list) {
			res.add(serialise(user));
		}
		return null;
	}


	public void create(User user) {
		user.setDeleted(false);
		user.setTime(LocalDateTime.now());
		DBUser dbUser = deserialise(user);
		dbuserRepository.save(dbUser);
		log.info("New user created with id = {}", dbUser.getUserid());
		String userid = dbUser.getUserid();
		walletService.create(userid);
		log.info("Wallet created with for user = {}", user.getEmail());
		basketService.create(userid);
		log.info("Basket created with for user = {}", user.getEmail());
	}

	public boolean update(User user) {
		String id = (new StringRandom()).generateRandomString(user.getId(), user.getName());
		DBUser toBeUpdated = dbuserRepository.findByUserid(id);

		if (toBeUpdated == null) {
			log.error("User with id {} doesn't exist", id);
			return false;
		}

		if (toBeUpdated.isDeleted()) {
			log.debug("User deleted...");
			return false;
		}

		Gson gson = new Gson();
		String usertostr = gson.toJson(toBeUpdated);
		Map toup = gson.fromJson(usertostr, Map.class);
		String userfromstr = gson.toJson(user);
		Map fromup = gson.fromJson(userfromstr, Map.class);
		fromup.forEach((k, v) -> toup.put(k, v));
		usertostr = gson.toJson(toup);
		User newuser = gson.fromJson(usertostr, User.class);
		dbuserRepository.save(deserialise(newuser));
		log.info("User details of {} updated successfully", user.getId());
		return true;
	}

	public void deleteUser(User user) {
		if (user.isDeleted()) {
			log.error("User already deleted !!");
			return;
		}
		log.info("User {} Deleted", user.getId());
		user.setDeleted(true);
		dbuserRepository.save(deserialise(user));
	}

	public boolean isInRecords(User newuser) {
		String userid = (new StringRandom()).generateRandomString(newuser.getId(), newuser.getName());
		DBUser user = dbuserRepository.findByUserid(userid);
		if (user == null) {
			log.error("No user exist with the details :{}", newuser);
			return false;
		}
		if (user != null) {
			log.info("User found : " + userid + user.getEmail());
		}
		DBUser user1 = dbuserRepository.findByEmail(newuser.getEmail());
		if (user1 != null) {
			log.info("User found : " + userid + user1.getEmail());
		}
		DBUser user2 = dbuserRepository.findByPhone(newuser.getPhone());
		if (user != null) {
			log.info("User found : " + userid + user2.getEmail());
		}
		return true;
	}

	public String findByUserName(String username) {
		DBUser user = dbuserRepository.findByEmail(username);
		if (user == null)
			return null;
		return user.getUserid();
	}


	public Page<DBUser> searchUsers(UserReq userReq) {
		GenericSpecificationBuilder<DBUser> builder = new GenericSpecificationBuilder<>();
		if (!StringUtils.isEmpty(userReq.getName())) {
			builder.with(DBuserSpecificationFactory.isIN("name", userReq.getName()));
		}
		if (!StringUtils.isEmpty(userReq.getEmail())) {
			builder.with(DBuserSpecificationFactory.isIN("email", userReq.getEmail()));
		}
		if (!StringUtils.isEmpty(userReq.getPhone())) {
			builder.with(DBuserSpecificationFactory.isIN("phone", userReq.getPhone()));
		}
		if (!StringUtils.isEmpty(userReq.getRole())) {
			builder.with(DBuserSpecificationFactory.isIN("role", userReq.getRole()));
		}
		if (!StringUtils.isEmpty(userReq.getLowerTime())) {
			builder.with(DBuserSpecificationFactory.isGreaterThanEqualTo("creationTime", userReq.getLowerTime()));
		}
		if (!StringUtils.isEmpty(userReq.getUpperTime())) {
			builder.with(DBuserSpecificationFactory.isLessThanEqualTo("creationTime", userReq.getUpperTime()));
		}
		Pageable pageable;
		if (!StringUtils.isEmpty(userReq.getSortType())) {
			if (userReq.getSortType().equals(SortType.ASC)) {
				pageable =
						PageRequest.of(userReq.getPage(), userReq.getSize(), Sort.by(userReq.getSortBy()).ascending());
			} else {
				pageable =
						PageRequest.of(userReq.getPage(), userReq.getSize(), Sort.by(userReq.getSortBy()).descending());
			}
		} else {
			pageable = PageRequest.of(userReq.getPage(), userReq.getSize(), Sort.by(userReq.getSortBy()));
		}
		try {
			return dbuserRepository.findAll(builder.build(), pageable);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
	}


	public User serialise(DBUser user) {
		GsonUtil gsonUtil = new GsonUtil();
		User resUser = (User) gsonUtil.convert(new User(), user);
		log.info("Serialised : " + user.getEmail());
		return resUser;
	}

	public DBUser deserialise(User user) {
		GsonUtil gsonUtil = new GsonUtil();
		DBUser dbUser = (DBUser) gsonUtil.convert(new DBUser(), user);
		String userid = (new StringRandom().generateRandomString(user.getId(), user.getName()));
		dbUser.setUserid(userid);
		log.info("Deserialised : " + user.getEmail());
		return dbUser;
	}

	public boolean checkIfUserExists(String id) {
		DBUser user = dbuserRepository.findByUserid(id);
		if (user == null || user.isDeleted)
			return true;
		return false;
	}

	public User findByUserid(String userid) throws Exception {
		try {
			DBUser user = dbuserRepository.findByUserid(userid);
			return serialise(user);
		} catch (Exception e) {
			return null;
		}
	}

}

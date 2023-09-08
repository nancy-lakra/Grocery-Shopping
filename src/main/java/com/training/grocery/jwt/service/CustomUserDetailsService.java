package com.training.grocery.jwt.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.training.grocery.user.hibernate.DBuserRepository;
import com.training.grocery.user.hibernate.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DBuserRepository repository;

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<SimpleGrantedAuthority> roles = null;
		if (username == null) {
			log.error("Username is null");
		}
		log.debug("Finding User with email {}", username);
		com.training.grocery.user.dbmodel.User user = userService.serialise(repository.findByEmail(username));
		if (user != null) {
			log.debug("Finding roles of {} ", username);
			roles = new ArrayList<>();
			roles.add(new SimpleGrantedAuthority(user.getRole().toString()));
			return new User(user.getEmail(), user.getPassword(), roles);
		}
		log.error("No user exists by {} email", username);
		throw new UsernameNotFoundException("User not found");

		/*
		 * if(username.equals("Nilesh")) { return new User("Nilesh", "Nilesh@123", new ArrayList<>()); } else { throw
		 * new UsernameNotFoundException("User not found"); }
		 */
	}
}

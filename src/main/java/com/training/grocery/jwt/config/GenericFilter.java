package com.training.grocery.jwt.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.training.grocery.jwt.utils.JwtUtil;
import com.training.grocery.user.hibernate.DBuserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@Order(1)
public class GenericFilter extends OncePerRequestFilter {

	public static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DBuserRepository repository;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		String requestTokenHeader = request.getHeader("Authorization");
		String jwtToken = null;
		String username = null;
		String userid = null;
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
			jwtToken = requestTokenHeader.substring(7);

			try {
				log.debug("Extracting email from token");
				username = jwtUtil.extractUsername(jwtToken);
			} catch (Exception e) {
				// TODO: handle exception
				log.error("Unable to extract user-email", e);
			}
		}
		// System.out.println("REACHED HERE" + username);
		try {
			if (username != null) {
				userid = String.valueOf(repository.findByEmail(username).getId());
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Extracted Username is NULL", e);
		}

		try {
			if (userid != null) {
				threadLocal.set(userid);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("UserId is NULL. ThreadLocal has no value", e);
		}
		filterChain.doFilter(request, response);
	}
}

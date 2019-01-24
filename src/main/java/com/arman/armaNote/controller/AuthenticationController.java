package com.arman.armaNote.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.arman.armaNote.config.JwtTokenUtil;
import com.arman.armaNote.model.LoginUser;
import com.arman.armaNote.model.User;
import com.arman.armaNote.service.UserService;

@RestController
@RequestMapping("/token")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager; // given by spring security, but defining bean is must so we
													     // created a bean of this in SecurityConfig.java

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/generate-token", method = RequestMethod.POST)
	public ResponseEntity<LoginUser> register(@RequestBody LoginUser loginUser, HttpServletResponse response)
			throws AuthenticationException {
		// HttpServletResponse will help us adding cookie in response.
		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// final User user = userService.findUserByEmail(loginUser.getEmail());
		final User user = userService.findUserByUsernameOrEmail(loginUser.getEmail());

		final String token = jwtTokenUtil.generateToken(user);
		// return ResponseEntity.ok(new AuthToken(token));

		Cookie cookie = new Cookie("Authorization", "Bearer " + token);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);

		loginUser.setProfileImg(
				"https://lh3.googleusercontent.com/-FVyJ-WChXTg/AAAAAAAAAAI/AAAAAAAAAAA/AAN31DWLtq0siZ-zRT0F9TPvXIDMmqsMVQ/s64-c-mo/photo.jpg");
		loginUser.setPassword("");
		loginUser.setFullName(user.getFirstName() + " " + user.getLastName());
		loginUser.setUserId(user.getId());
		loginUser.setUsername(user.getUsername());
		// note that we are not adding response object to ResponseEntity. Bcz they are
		// automatically interconnected by the framework.
		ResponseEntity<LoginUser> responseEntity = new ResponseEntity<LoginUser>(loginUser, HttpStatus.OK);
		return responseEntity;

	}

}

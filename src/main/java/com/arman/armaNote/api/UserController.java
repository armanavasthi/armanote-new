package com.arman.armaNote.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.model.User;
import com.arman.armaNote.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@CrossOrigin(origins = "http://localhost:4200") // check if this is still needed (as we now have CorsFilter.java)
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getUsers() {
		Map<String, Object> response = new HashMap<>();
		List<User> users = userService.getAllUsers();
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.add("success", "true");

		response.put("success", true);
		response.put("Data", users);

		return new ResponseEntity<Map<String, Object>>(response, httpHeader, HttpStatus.OK);
	}

	@GetMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	public User getUser(@PathVariable String email) {
		// variable name is email, but id/username/email can be passed
		// write a proper code (using Optional probably) to handle the case where no
		// user found (404) or 500 etc.
		if (email.matches("\\d+")) {
			long userId = Long.parseLong(email);
			// if(userId < 1) {
			//
			// }
			return userService.findUserById(userId);
		}
		return userService.findUserByUsernameOrEmail(email);
	}
	
	@PostMapping(value = "/registration", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> saveUser(@RequestBody User user) {
		HttpStatus httpStatus = null;
		User currentUser = userService.getCurrentUser();
		
		/*
		 * 1. check the roles
		 * 2. if only "user" then proceed
		 * 3. otherwise see who created (currently logged in person) if he is admin the only allow otherwise 403 error
		 * 4. if already a user with same username or email then return back with warning
		 * 5. if alright then give the roles ids to role bcz role id will be null from frontend (think for better approach)
		 * 6. save the user
		 */
		
		Optional<Role> nonUser = user.getRoles().stream()
			.filter(role ->	!role.getRole().equals("USER"))
			.findFirst();
		
		if (nonUser.isPresent()) {			
			if (currentUser == null || 
						!currentUser.getRoles().stream()
							.filter(role -> role.getRole().equals("ADMIN")).findFirst().isPresent()) {
				httpStatus = HttpStatus.UNAUTHORIZED;
			}
		}
		
		if (httpStatus == null) {
			// if already a user with same username or email then return back with warning
			User oldUser = userService.findUserByUsernameOrEmail(user.getEmail(), user.getUsername());
			
			if (oldUser != null) {
				httpStatus = HttpStatus.CONFLICT; // https://stackoverflow.com/a/3826024/7456022
			}
			
			if (httpStatus == null) {
				user.setCreator(currentUser);
				userService.saveUser(user);
				httpStatus = HttpStatus.OK;
			}
		}
		
		return new ResponseEntity<Boolean>(httpStatus);
	}

	@PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> updateUser(@RequestBody User user) {
		User userFromDB = null;
		String currentUsername = userService.getCurrentUsername();
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpStatus httpStatus = null;

		if (user.getEmail() == null || user.getEmail().isEmpty()) {
			httpHeaders.add("message", "Please send proper user details");
			httpStatus = HttpStatus.NOT_FOUND;
		} else {
			userFromDB = userService.findUserByUsernameOrEmail(user.getEmail());
		}

		if (userFromDB == null) {
			httpHeaders.add("message", "There is no user with this email");
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (!user.getUsername().equalsIgnoreCase(currentUsername)) {
			httpHeaders.add("message", "You are not authorized to change other user's informations");
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (!user.getUsername().equals(userFromDB.getUsername())) {
			httpHeaders.add("message", "You cannot change your username");
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else {
			userService.saveUser(user);
			httpHeaders.add("message", "User details are updated successfully");
			httpStatus = HttpStatus.OK;
		}
		return new ResponseEntity<Boolean>(true, httpHeaders, httpStatus);
	}
	
	@GetMapping(value="/logincheck")
	public ResponseEntity<Boolean> loginCheck() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}

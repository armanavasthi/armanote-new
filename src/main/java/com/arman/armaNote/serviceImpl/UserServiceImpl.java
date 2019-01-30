package com.arman.armaNote.serviceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.model.User;
import com.arman.armaNote.repository.RoleRepository;
import com.arman.armaNote.repository.UserRepository;
import com.arman.armaNote.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public User findUserByUsernameOrEmail(String email) {
		return userRepository.findByUsernameOrEmail(email);
	}
	
	@Override
	public User findUserByUsernameOrEmail(String email, String username) {
		return userRepository.findByUsernameOrEmail(email, username);
	}
	
	public User findUserById(long userId) {
		Optional<User> result = userRepository.findById(userId);
		
		return result.orElse(null); // send null though is not a good approach. better throw some exception.
	}
	
	public void saveUser(User user) {
		//you may also add that password follows a specific regex (like no space, must have special char)
		
		// encrypting the password
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		
		// if user doesn't have any role then by default assign "USER" role
		if (user.getRoles() == null) {
			Role role = roleRepository.findByRole("USER");
			user.setRoles(new HashSet<Role>(Arrays.asList(role)));
		}
		
		userRepository.save(user);
	}
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	@Override
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = null;
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    Object principal = authentication.getPrincipal();
		    if (principal instanceof User) currentUser= ((User) principal);	    
		}
		return currentUser;
	}
	
	// below code is taken from http://www.baeldung.com/get-user-in-spring-security
	// there are other nice ways to get current user given in the link above. Please do read.
	@Override
	public String getCurrentUsername() {
		String currentUserName = null;
		User user = this.getCurrentUser();
		if (user != null) currentUserName = user.getUsername();
		return currentUserName;
	}
	
	public String getUserRole(String email) {
		return userRepository.findUserRole(email);
	}
}

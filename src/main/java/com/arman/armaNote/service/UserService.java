package com.arman.armaNote.service;

import java.util.List;

import com.arman.armaNote.model.User;

public interface UserService {
	public User findUserByUsernameOrEmail(String email);
	public User findUserByUsernameOrEmail(String email, String username);
	public User findUserById(long userId);
	public String getUserRole(String email);
	public void saveUser(User user);
	public List<User> getAllUsers();
	public User getCurrentUser();
	public String getCurrentUsername();
}

package com.arman.armaNote.seerviceImpl;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.model.User;
import com.arman.armaNote.repository.RoleRepository;
import com.arman.armaNote.repository.UserRepository;
import com.arman.armaNote.service.UserService;
import com.arman.armaNote.serviceImpl.UserServiceImpl;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

public class UserServiceImplTest {

	UserService userService;

	@Mocked
	UserRepository userRepository;

	@Mocked
	RoleRepository roleRepository;

	@Mocked
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mocked
	SecurityContext securityContext;
	
	@Mocked
	Authentication authentication;
	
	@Mocked
	AnonymousAuthenticationToken anonymousAuthentication;

	@BeforeMethod
	public void init() {
		userService = new UserServiceImpl();
		ReflectionTestUtils.setField(userService, "userRepository", userRepository);
		ReflectionTestUtils.setField(userService, "roleRepository", roleRepository);
		ReflectionTestUtils.setField(userService, "bCryptPasswordEncoder", bCryptPasswordEncoder);
	}

	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase1_1(List<User> users) {

		new Expectations() {
			{
				userRepository.findByUsernameOrEmail(anyString);
				result = users.get(0);
			}
		};

		assertEquals(userService.findUserByUsernameOrEmail("abc").getFirstName(), "testFirstName");
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase1_2(List<User> users) {
		assertEquals(userService.findUserByUsernameOrEmail(""), null);
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase2_1(List<User> users) {

		new Expectations() {
			{
				userRepository.findByUsernameOrEmail(anyString, anyString);
				result = users.get(0);
			}
		};

		assertEquals(userService.findUserByUsernameOrEmail("abc", "def").getFirstName(), "testFirstName");
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase2_2(List<User> users) {

		assertEquals(userService.findUserByUsernameOrEmail("", ""), null);
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase2_3(List<User> users) {

		new Expectations() {
			{
				userRepository.findByUsernameOrEmail(anyString);
				result = users.get(0);
			}
		};

		assertEquals(userService.findUserByUsernameOrEmail("", "def").getFirstName(), "testFirstName");
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase2_4(List<User> users) {

		new Expectations() {
			{
				userRepository.findByUsernameOrEmail(anyString);
				result = users.get(0);
			}
		};

		assertEquals(userService.findUserByUsernameOrEmail("abc", "").getFirstName(), "testFirstName");
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByIdCase1(List<User> users) {
		new Expectations() {
			{
				userRepository.findById(anyLong);
				result = Optional.ofNullable(null);
			}
		};
		assertEquals(userService.findUserById(1), null);
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByIdCase2(List<User> users) {
		new Expectations() {
			{
				userRepository.findById(anyLong);
				result = Optional.of(users.get(0));
			}
		};
		assertEquals(userService.findUserById(1).getFirstName(), "testFirstName");
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByIdCase3(List<User> users) {
		assertEquals(userService.findUserById(-1), null);
	}

	@Test(enabled = true, groups = "UNIT", dataProvider = "usersAndRolesDataProvider")
	public void saveUserCase1(List<User> users, Set<Role> role) {

		new Expectations() {
			{
				bCryptPasswordEncoder.encode(anyString);
				result = "te$tenc0ded$tr1ng";
			}
			{
				roleRepository.findByRole(anyString);
				result = role;
			}
			{
				userRepository.save((User) any);
				result = users.get(0);
			}
		};
		userService.saveUser(users.get(0));
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersAndRolesDataProvider")
	public void saveUserCase2(List<User> users, Set<Role> roles) { 
		
		new Expectations() {
			{
				bCryptPasswordEncoder.encode(anyString);
				result = "te$tenc0ded$tr1ng";
			}
			{
				userRepository.save((User) any);
				result = users.get(1);
			}
		};
		userService.saveUser(users.get(1));
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getAllUsersCase1(List<User> users) {
		new Expectations() {
			{
				userRepository.findAll();
				result = users;
			}
		};
		assertEquals(userService.getAllUsers().get(0).getFirstName(), "testFirstName");
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getCurrentUserCase1(List<User> users) {
		new Expectations(SecurityContextHolder.class) {
			{
				SecurityContextHolder.getContext();
				result = securityContext;
			}
			{
				securityContext.getAuthentication();
				result = authentication; 
			}
			{
				authentication.getPrincipal();
				result = users.get(1);
			}
		};
		assertEquals(userService.getCurrentUser().getEmail(), "testuser@test.com");
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getCurrentUserCase2(List<User> users) {
		new Expectations(SecurityContextHolder.class) {
			{
				SecurityContextHolder.getContext();
				result = securityContext;
			}
			{
				securityContext.getAuthentication();
				result = anonymousAuthentication; 
			}
		};
		assertEquals(userService.getCurrentUser(), null);
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getCurrentUserCase3(List<User> users) {
		new Expectations(SecurityContextHolder.class) {
			{
				SecurityContextHolder.getContext();
				result = securityContext;
			}
			{
				securityContext.getAuthentication();
				result = authentication; 
			}
			{
				authentication.getPrincipal();
				result = "NOTUSER";
			}
		};
		assertEquals(userService.getCurrentUser(), null);
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getCurrentUsernameCase1(List<User> users) {
		final UserServiceImpl userService = new UserServiceImpl();
		
		new NonStrictExpectations(userService) {
			{
				userService.getCurrentUser();
				result = users.get(1);
			}
		};
		
		assertEquals(userService.getCurrentUsername(), "testuser");
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getCurrentUsernameCase2(List<User> users) {
		// https://stackoverflow.com/a/19926571/7456022
		final UserServiceImpl userService = new UserServiceImpl();
		
		new NonStrictExpectations(userService) {
			{
				userService.getCurrentUser();
				result = null;
			}
		};
		
		assertEquals(userService.getCurrentUsername(), null);
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersAndRolesDataProvider")
	public void getUserRolesCase1(List<User> users, Set<Role> roles) {
		String[] rolesString = {"ADMIN", "USER"};
		new Expectations() {
			{
				userRepository.findUserRoles(anyString);
				result = rolesString;
			}
		};
		
		assertEquals(userService.getUserRoles(users.get(1).getEmail())[0], "ADMIN");
	}
	
	@Test(enabled=true, groups = "UNIT", dataProvider = "usersAndRolesDataProvider")
	public void getMostPriorRoleCase1(List<User> users, Set<Role> roles) {
		new Expectations() {
			{
				userRepository.findMaxPriorRole(anyString);
				result = "ADMIN";
			}
		};
		
		assertEquals(userService.getMostPriorRole(users.get(1).getEmail()), "ADMIN");
	}

	@DataProvider(name = "usersDataProvider")
	public Object[][] usersDataProvider() {
		List<User> users = new ArrayList<>();

		User user0 = new User();
		user0.setFirstName("testFirstName");

		User user1 = new User();
		user1.setActive(1);
		user1.setEmail("testuser@test.com");
		user1.setUsername("testuser");

		users.add(user0);
		users.add(user1);

		return new Object[][] { { users } };
	}

	@DataProvider(name = "usersAndRolesDataProvider")
	public Object[][] usersRoleDataProvider() {

		List<User> users = new ArrayList<>();

		User user0 = new User();
		user0.setFirstName("testFirstName");

		User user1 = new User();
		user1.setActive(1);
		user1.setEmail("testuser@test.com");
		user1.setUsername("testuser");
		user1.setPassword("testPassword");
		
		Role role = new Role();
		role.setId(1);
		role.setRole("test");
		
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user1.setRoles(roles);

		users.add(user0);
		users.add(user1);

		return new Object[][] { { users, roles } };
	}

}

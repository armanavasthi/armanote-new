package com.arman.armaNote.seerviceImpl;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

public class UserServiceImplTest {

	UserService userService;

	@Mocked
	UserRepository userRepository;

	@Mocked
	RoleRepository roleRepository;

	@Mocked
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeMethod
	public void init() {
		userService = new UserServiceImpl();
		ReflectionTestUtils.setField(userService, "userRepository", userRepository);
		ReflectionTestUtils.setField(userService, "roleRepository", roleRepository);
		ReflectionTestUtils.setField(userService, "bCryptPasswordEncoder", bCryptPasswordEncoder);
	}

	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void findUserByUsernameOrEmailCase1(List<User> users) {

		new Expectations() {
			{
				userRepository.findByUsernameOrEmail(anyString);
				result = users.get(0);
			}
		};

		// check how to call a service method directly in jmockit
		assertEquals(userService.findUserByUsernameOrEmail("abc"), users.get(0));
	}

	@Test(enabled = true, groups = "UNIT", dataProvider = "usersAndRolesDataProvider")
	public void saveUserCase1(List<User> users, Role role) {

		new Expectations() {
			{
				bCryptPasswordEncoder.encode(anyString);
				result = anyString;
			}
			{
				roleRepository.findByRole(anyString);
				result = role;
			}
			{
				userRepository.save((User) any);
				result = (User) any;
			}
		};
		userService.saveUser(users.get(1));
	}

	@DataProvider(name = "usersDataProvider")
	public Object[][] usersDataProvider() {
		List<User> users = new ArrayList<>();

		User user1 = new User();
		user1.setFirstName("testFirstName");

		User user2 = new User();
		user2.setActive(1);
		user2.setEmail("testuser@test.com");
		user2.setUsername("testuser");

		users.add(user1);
		users.add(user2);

		return new Object[][] { { users } };
	}

	@DataProvider(name = "usersAndRolesDataProvider")
	public Object[][] usersRoleDataProvider() {

		List<User> users = new ArrayList<>();

		User user1 = new User();
		user1.setFirstName("testFirstName");

		User user2 = new User();
		user2.setActive(1);
		user2.setEmail("testuser@test.com");
		user2.setUsername("testuser");
		user2.setPassword("testPassword");

		users.add(user1);
		users.add(user2);

		Role role = new Role();
		role.setId(1);
		role.setRole("test");

		return new Object[][] { { users, role } };
	}

}

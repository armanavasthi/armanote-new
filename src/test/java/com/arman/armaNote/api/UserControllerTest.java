package com.arman.armaNote.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.model.User;
import com.arman.armaNote.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mockit.Expectations;
import mockit.Mocked;

public class UserControllerTest {
	
	UserController userController;
	
	private MockMvc mockMvc;
	
	@Mocked
	private UserService userService;
	
	@BeforeMethod
	public void init() {
		userController = new UserController();
		
		ReflectionTestUtils.setField(userController, "userService", userService);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getUsersCase1(List<User> users) throws Exception {
		new Expectations() {
			{
				userService.getAllUsers();
				result = users;
			}
		};
		
		mockMvc.perform(get("/api/user/"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$['success']", is(true)))
        .andExpect(jsonPath("$['Data'][0]['firstName']", is("testFirstName")));	
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getUserCase1(List<User> users) throws Exception {
		new Expectations() {
			{
				userService.findUserById(anyLong);
				result = users.get(0);
			}
		};
		
		mockMvc.perform(get("/api/user/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("firstName", is("testFirstName")));	
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void getUserCase2(List<User> users) throws Exception {
		new Expectations() {
			{
				userService.findUserByUsernameOrEmail(anyString);
				result = users.get(0);
			}
		};
		
		mockMvc.perform(get("/api/user/testusername"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("firstName", is("testFirstName")));	
	}
	
	/*
	 * When we send user without his email info
	 * Result: user not found with this email.
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase1(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(0))))
		.andExpect(status().isNotFound());
	}
	
	/*
	 * When we send user with email as empty string
	 * Result: user not found with this email.
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase2(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		users.get(0).setEmail("");
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(0))))
		.andExpect(status().isNotFound());
	}
	
	/*
	 * When the user we want to update is not there in db
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase3(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		new Expectations() {{
				userService.findUserByUsernameOrEmail(anyString);
				result = null;
		}};
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isNotFound());
	}
	
	/*
	 * When user is there in db but he is not the current user.
	 * Result: can't update other's info
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase4(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		new Expectations() {{
				userService.findUserByUsernameOrEmail(anyString);
				result = users.get(2);
		}};
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(2))))
		.andExpect(status().isUnauthorized());
	}
	
	/*
	 * When user is there in db and he is the current user.
	 * Result: user will be saved
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase5(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		new Expectations() {{
				userService.findUserByUsernameOrEmail(anyString);
				result = users.get(1);
		}};
		
		new Expectations() {{
			userService.saveUser((User)any);;
		}};
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isOk());
	}
	
	/*
	 * When user is there in db but we are trying to change his username.
	 * Result: username cannot be changed
	 */
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase6(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "differentUsername";
		}};
		
		new Expectations() {{
				userService.findUserByUsernameOrEmail(anyString);
				result = users.get(1);
		}};
		
		String userJson = getUserJson(users.get(1));
		userJson = userJson.replaceAll("testuser", "differentUsername");
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
		.andExpect(status().isUnauthorized());
	}
	
	/*
	 * When normal "user" is being registered.
	 * Success Scenario: User getting saved.
	 */
	@Test(enabled=true, groups="UNIT", dataProvider="usersDataProvider")
	public void saveUserCase1(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUser();
			result = null;
		}};
		
		new Expectations() {{
			userService.findUserByUsernameOrEmail(anyString, anyString);
			result =  null;
		}};
		
		new Expectations() {{
			userService.saveUser(users.get(2));
		}};

		mockMvc.perform(post("/api/user/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(2))))
		.andExpect(status().isOk());
	}
	
	/*
	 * When normal "user" is being registered.
	 * Success Scenario: User not getting saved, we get unauthorized.
	 */
	@Test(enabled=true, groups="UNIT", dataProvider="usersDataProvider")
	public void saveUserCase2(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUser();
			result = null;
		}};

		mockMvc.perform(post("/api/user/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isUnauthorized());
	}
	
	/*
	 * When normal "user" is being registered.
	 * Success Scenario: User getting saved, bcz current user is admin.
	 */
	@Test(enabled=true, groups="UNIT", dataProvider="usersDataProvider")
	public void saveUserCase3(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUser();
			result = users.get(3);
		}};
		
		new Expectations() {{
			userService.findUserByUsernameOrEmail(anyString, anyString);
			result =  null;
		}};
		
		new Expectations() {{
			userService.saveUser(users.get(1));
		}};

		mockMvc.perform(post("/api/user/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isOk());
	}
	
	/*
	 * When normal "user" is being registered.
	 * Success Scenario: User getting saved, bcz current user is admin.
	 */
	@Test(enabled=true, groups="UNIT", dataProvider="usersDataProvider")
	public void saveUserCase4(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUser();
			result = users.get(3);
		}};
		
		new Expectations() {{
			userService.findUserByUsernameOrEmail(anyString, anyString);
			result =  users.get(1);
		}};

		mockMvc.perform(post("/api/user/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isConflict());
	}
	
	/*
	 * When we want to register with non-user role, current user is not null but he is not an admin.
	 * Success Scenario: Not authorized
	 */
	@Test(enabled=true, groups="UNIT", dataProvider="usersDataProvider")
	public void saveUserCase5(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUser();
			result = users.get(2);
		}};

		mockMvc.perform(post("/api/user/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getUserJson(users.get(1))))
		.andExpect(status().isUnauthorized());
	}
	
	public static String getUserJson(User user) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(user);
	}
	
	@DataProvider(name = "usersDataProvider")
	private Object[][] usersDataProvider() {
		Role role1 = new Role();
		role1.setId(1);
		role1.setRole("TestRole1");
		Role role2 = new Role();
		role2.setId(1);
		role2.setRole("USER");
		Role role3 = new Role();
		role3.setId(1);
		role3.setRole("ADMIN");
		
		Set<Role> roles = new HashSet<>();
		roles.add(role1);
		roles.add(role2);
		
		Set<Role> roles2 = new HashSet<>();
		roles2.add(role2);
		
		Set<Role> roles3 = new HashSet<>();
		roles3.add(role1);
		roles3.add(role2);
		roles3.add(role3);
		
		User user0 = new User();
		user0.setFirstName("testFirstName");
		
		User user1 = new User();
		user1.setFirstName("Test");
		user1.setLastName("Man");		
		user1.setEmail("testuser@test.com");
		user1.setUsername("testuser");
		user1.setPassword("testpwd");
		user1.setActive(1);
		user1.setRoles(roles);
		
		
		User user2 = new User();
		user2.setFirstName("Best");
		user2.setLastName("Man");		
		user2.setEmail("bestuser@test.com");
		user2.setUsername("bestuser");
		user2.setPassword("bestpwd");
		user2.setActive(1);
		user2.setRoles(roles2);
		
		User user3 = new User();
		user3.setFirstName("Admin");
		user3.setLastName("User");		
		user3.setEmail("adminuser@test.com");
		user3.setUsername("adminuser");
		user3.setPassword("adminpwd");
		user3.setActive(1);
		user3.setRoles(roles3);
		
		
		List<User> users = new ArrayList<User>();
		users.add(user0);
		users.add(user1);
		users.add(user2);
		users.add(user3);		
		
		return new Object[][] {{ users }};
	}
	
}

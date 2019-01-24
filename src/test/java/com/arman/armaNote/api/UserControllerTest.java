package com.arman.armaNote.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.arman.armaNote.model.User;
import com.arman.armaNote.service.UserService;
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
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase1(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
//		new Expectations() {
//			{
//				userService.getCurrentUsername();
//				result = users.get(0);
//			}
//		};
//		
//		new Expectations() {
//			{
//				userService.findUserByUsernameOrEmail(anyString);
//				result = null;
//			}
//		};
		
		ObjectMapper mapper = new ObjectMapper();
		String userjson = mapper.writeValueAsString(users.get(0));
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userjson))
		.andExpect(status().isNotFound());
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "usersDataProvider")
	public void updateUserCase2(List<User> users) throws Exception {
		new Expectations() {{
			userService.getCurrentUsername();
			result = "testuser";
		}};
		
		new Expectations() {
			{
				userService.findUserByUsernameOrEmail(anyString);
				result = users.get(1);
			}
		};
		
		ObjectMapper mapper = new ObjectMapper();
		String userjson = mapper.writeValueAsString(users.get(1));
		
		mockMvc.perform(put("/api/user/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userjson))
		.andExpect(status().isNotFound());
	}
	
	
	
	@DataProvider(name = "usersDataProvider")
	private Object[][] usersDataProvider() {
		User user1 = new User();
		user1.setFirstName("testFirstName");
		User user2 = new User();
		user2.setActive(1);
		user2.setEmail("testuser@test.com");
		user2.setUsername("testuser");
		List<User> users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
		
		return new Object[][] {{ users }};
	}
	
}

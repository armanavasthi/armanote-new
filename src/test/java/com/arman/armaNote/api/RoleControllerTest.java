package com.arman.armaNote.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
// import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.service.RoleService;

import mockit.Expectations;
import mockit.Mocked;

public class RoleControllerTest {
	RoleController roleController;
	
	MockMvc mockMvc;
	
	@Mocked
	RoleService roleService;
	
	@BeforeMethod
	public void init() {
		roleController = new RoleController();
		ReflectionTestUtils.setField(roleController, "roleService", roleService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
	}
	
	@Test(enabled=true, groups= {"UNIT"} /*, dataProvider= "roleDataProvider"*/)
	public void getAllScenario1() throws Exception {
		new Expectations() {{
			roleService.getAllRoles();
			result = new ArrayList<>().add((Role)any);
		}};
		
		
		
		mockMvc.perform(get("/api/role/"))
			.andExpect(status().isOk());
	}
	
	/*@DataProvider()
	public Object[][] roleDataProvider() {
		return null;
	}*/
}

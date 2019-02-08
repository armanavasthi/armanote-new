package com.arman.armaNote.seerviceImpl;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.repository.RoleRepository;
import com.arman.armaNote.service.RoleService;
import com.arman.armaNote.serviceImpl.RoleServiceImpl;

import mockit.Expectations;
import mockit.Mocked;

public class RoleServiceImplTest {
	
	@Mocked
	RoleRepository roleRepository;
	
	RoleService roleService;
	
	@BeforeMethod
	public void init() {
		roleService = new RoleServiceImpl();
		ReflectionTestUtils.setField(roleService, "roleRepository", roleRepository);
	}
	
	@Test(enabled = true, groups = "UNIT", dataProvider = "rolesDataProvider")
	public void getAllRolesCase1(Set<Role> roles) {
		List<Role> allRoles= new ArrayList<>(roles);
		new Expectations() {
			{
				roleRepository.findAll();
				result = allRoles;
			}
		};
		assertEquals(roleService.getAllRoles().get(1).getRole(), "USER");
	}
	
	@DataProvider(name = "rolesDataProvider")
	private Object[][] rolesDataProvider() {
		Role role0 = new Role();
		role0.setId(1);
		role0.setRole("test");
		
		Role role1 = new Role();
		role1.setId(2);
		role1.setRole("USER");
		
		Set<Role> roles = new HashSet<>();
		roles.add(role0);
		roles.add(role1);
		return new Object[][] {{ roles }};
	}
}

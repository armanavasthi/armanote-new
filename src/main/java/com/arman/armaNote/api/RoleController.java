package com.arman.armaNote.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arman.armaNote.model.Role;
import com.arman.armaNote.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	@GetMapping(value="/")
	public ResponseEntity<List<Role>> getAll() {
		return new ResponseEntity<List<Role>>(roleService.getAllRoles(), HttpStatus.OK);
	}
}

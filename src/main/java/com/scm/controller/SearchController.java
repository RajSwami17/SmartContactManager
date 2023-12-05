package com.scm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.repo.ContactRepo;
import com.scm.repo.UserRepo;

@RestController
public class SearchController 
{
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	//Search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query,Principal principal)
	{
		System.out.println("Query :" + query);
		User user = userRepo.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepo.findByfirstNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}

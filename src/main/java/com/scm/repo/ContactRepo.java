package com.scm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactRepo extends JpaRepository<Contact,Integer>
{
	
	//Pagination 
	@Query("from Contact as c where c.user.id=:userId")
	//public List<Contact> findContactsByUser(@Param("userId") int userId);
	//current page - page
	//Contact per page - 8
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	//@Query("from Contact as c where c.user.name=:name")
	public List<Contact> findByfirstNameContainingAndUser(String name,User user);
	
}
 	
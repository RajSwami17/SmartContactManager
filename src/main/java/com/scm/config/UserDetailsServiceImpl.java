package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.scm.entities.User;
import com.scm.repo.UserRepo;

public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		// Fetching user from database
		User user = userRepo.getUserByUserName(username);
		if(user==null) 
		{
			throw new UsernameNotFoundException("User Not Found..!!");
		}
		//if user not null then it will check the entered details with user details data for login.
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return customUserDetails;
	}

}

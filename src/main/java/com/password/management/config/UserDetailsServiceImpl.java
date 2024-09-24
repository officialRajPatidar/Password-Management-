package com.password.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.password.management.entity.User;
import com.password.management.repo.UserRepo;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired 
	private UserRepo repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = repo.findByUsername(username);
		
		if(user== null) {
			throw new UsernameNotFoundException("could not  found user !!");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
	}

}

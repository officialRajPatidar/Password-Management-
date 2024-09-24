package com.password.management.service;

import com.password.management.entity.User;

public interface UserService {

	void saveUser(User user);
	
	User findByUsername(String username);
	
	 User findByEmail(String email);
}

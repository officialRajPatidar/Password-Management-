package com.password.management.service.impl;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.password.management.entity.User;
import com.password.management.repo.UserRepo;
import com.password.management.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo repo;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private PasswordPolicyService passwordPolicyService;

    
    @Transactional
    @Override
    public void saveUser(User user) {
        // Check for duplicate username and email
        if (repo.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (repo.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();
        user.setId(userId);
        
        // Encode the password
        String encodedPassword = encoder.encode(user.getPassword());
        
        // Validate the password policy
        passwordPolicyService.isPasswordValid(user.getPassword(), user.getUsername(), user.getPreviousPasswords());

        // Set the encoded password and expiration date
        user.setPassword(encodedPassword);
        user.setPasswordExpirationDate(LocalDate.now().plusMonths(3));  // Set expiration date
        
        // Save the user to the database
        repo.save(user);
    }
    @Override
    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

	@Override
	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return repo.findByEmail(email);
	}
    
  

    // Additional methods for handling password expiration and locking accounts
}

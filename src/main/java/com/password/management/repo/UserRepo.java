package com.password.management.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.password.management.entity.User;

@EnableJpaRepositories
public interface UserRepo  extends JpaRepository<User, String>{
	
	User findByUsername(String username);
  User findByEmail(String email);
}

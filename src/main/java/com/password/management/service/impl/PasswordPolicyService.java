 package com.password.management.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

    public boolean isPasswordValid(String password, String username, List<String> previousPasswords) {
        // Check for strong password (min 8 characters, 1 uppercase, 1 number, 1 special character)
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(passwordPattern)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, include one uppercase letter, one number, and one special character.");
        }

        // Check if username is part of the password
        if (password.contains(username)) {
            throw new IllegalArgumentException("Password must not contain the username.");
        }

        // Check if the password has been used before
        if (previousPasswords.contains(password)) {
            throw new IllegalArgumentException("Password has been used before.");
        }

        return true;
    }
}

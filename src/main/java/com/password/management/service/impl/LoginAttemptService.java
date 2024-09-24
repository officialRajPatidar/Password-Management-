package com.password.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.password.management.entity.User;
import com.password.management.repo.UserRepo;

@Service
public class LoginAttemptService {

    @Autowired
    private UserRepo userRepo;

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailAttempts);

        if (newFailAttempts >= 3) {
            user.setAccountLocked(true);
        }

        userRepo.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        userRepo.save(user);
    }

    public void unlockAfterTime(User user) {
        // Logic to unlock after 1 hour, or add @Scheduled to automatically unlock
    }
}

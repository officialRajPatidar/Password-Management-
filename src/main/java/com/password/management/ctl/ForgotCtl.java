package com.password.management.ctl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.password.management.entity.User;
import com.password.management.repo.UserRepo;
import com.password.management.service.impl.EmailService;

@Controller
public class ForgotCtl {

    Random random = new Random();

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo repo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // To handle password encoding

    // Open email form
    @GetMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    // Send OTP
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        User user = repo.findByEmail(email);

        if (user == null) {
            session.setAttribute("message", "User with this email does not exist!");
            return "forgot_email_form";
        }

        int otp = random.nextInt(999999);
        String subject = "OTP From PMA";
        String message = "<h1>OTP = " + otp + "</h1>";

        boolean flag = emailService.sendEmail(subject, message, email);

        if (flag) {
            session.setAttribute("email", email);
            session.setAttribute("otp", otp);
            return "verify_otp_form";
        } else {
            session.setAttribute("message", "Check your email ID!!");
            return "forgot_email_form";
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
        Integer sessionOtp = (Integer) session.getAttribute("otp");
        String email = (String) session.getAttribute("email");

        if (sessionOtp != null && sessionOtp == otp) {
            User user = repo.findByEmail(email);
            if (user == null) {
                session.setAttribute("message", "User does not exist with this email!!");
                return "forgot_email_form";
            }
            return "password_change_form"; // Proceed to change password form
        } else {
            session.setAttribute("message", "You have entered the wrong OTP!");
            return "verify_otp_form";
        }
    }

    // Change Password
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("oldPassword") String oldPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            redirectAttributes.addFlashAttribute("message", "Session expired. Please start the process again.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/forgot"; // Redirect to forgot page
        }

        User user = repo.findByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/forgot";
        }

        // Validate old password
        if (!user.getPassword().equals(oldPassword)) {
            redirectAttributes.addFlashAttribute("message", "Old password is incorrect.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password";
        }

        // Check if new password is the same as old password
        if (user.getPassword().equals(newPassword)) {
            redirectAttributes.addFlashAttribute("message", "New password cannot be the same as the old password.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password";
        }

        // Validate new password strength
        if (!isStrongPassword(newPassword)) {
            redirectAttributes.addFlashAttribute("message", "New password must be at least 8 characters long, contain uppercase and lowercase letters, numbers, and special characters.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password";
        }

        // Update password and expiration date
        user.setPassword(newPassword);  // Apply password encryption here if needed
        user.setPasswordExpirationDate(LocalDate.now());
        repo.save(user);

        // Success message
        redirectAttributes.addFlashAttribute("message", "Password successfully updated! Please log in.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/login"; // Redirect to login page after success
    }


    // Method to check if the password is strong
    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*[0-9].*") && 
               password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
}

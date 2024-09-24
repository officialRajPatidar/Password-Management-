package com.password.management.ctl;

import com.password.management.entity.User;
import com.password.management.service.UserService;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
@Controller
public class UserCtl {

    @Autowired
    private UserService service;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDetailsService userDetailsService; 

    @GetMapping("/")
    public String home() {
        return "home";  // Returns home.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // Display registration form (JSP or Thymeleaf)
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Check for form validation errors
        if (result.hasErrors()) {
            return "register";  // Return to the registration form if there are errors
        }

        // Check for duplicate username or email
        if (service.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        if (service.findByEmail(user.getEmail()) != null) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "register";
        }

        // Check password strength
        if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            result.rejectValue("password", "error.user", "Password must be at least 8 characters, include 1 uppercase, lowercase, digits, and 1 special character");
            return "register";
        }

        // Save user with encrypted password
        service.saveUser(user);
        return "redirect:/login";  // Redirect to login page after successful registration
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login"; // Display login form
    }

    @GetMapping("/welcome")
    public String showWelcomePage(Model model, Principal principal) {
        // Get logged-in user's username from Principal
        String username = principal.getName(); 
        model.addAttribute("username", username);
        return "welcome"; // Points to welcome.html
    }


}

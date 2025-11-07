package com.examly.springapp.controller;

import com.examly.springapp.configuration.*;
import com.examly.springapp.model.User;
import com.examly.springapp.Service.UserService;
import com.examly.springapp.dto.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.registration.key:123456}")
    private String adminRegistrationKey;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        try {
            System.out.println("Registration request received - Role: " + registrationRequest.getRole() + ", AdminKey: " + (registrationRequest.getAdminKey() != null ? "***" : "null"));
            System.out.println("Configured admin key: " + adminRegistrationKey);
            
            User user = new User();
            user.setFirstName(registrationRequest.getFirstName());
            user.setLastName(registrationRequest.getLastName());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(registrationRequest.getPassword());
            
            // Determine user role
            User.Role requestedRole = User.Role.USER;
            if (registrationRequest.getRole() != null) {
                try {
                    requestedRole = User.Role.valueOf(registrationRequest.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    requestedRole = User.Role.USER;
                }
            }
            
            // Validate admin key if trying to register as ADMIN
            if (requestedRole == User.Role.ADMIN) {
                String providedKey = registrationRequest.getAdminKey();
                if (providedKey == null || providedKey.trim().isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Admin registration key is required for admin accounts");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
                
                // Trim both keys for comparison to avoid whitespace issues
                String trimmedProvidedKey = providedKey.trim();
                String trimmedAdminKey = adminRegistrationKey != null ? adminRegistrationKey.trim() : "";
                
                // Debug logging
                System.out.println("Admin key validation - Expected: '" + trimmedAdminKey + "', Provided: '" + trimmedProvidedKey + "'");
                System.out.println("Keys match: " + trimmedAdminKey.equals(trimmedProvidedKey));
                
                if (!trimmedAdminKey.equals(trimmedProvidedKey)) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid admin registration key. Expected: " + trimmedAdminKey.length() + " chars, Got: " + trimmedProvidedKey.length() + " chars");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
                
                // Key is valid, allow admin registration
                System.out.println("Admin key validated successfully!");
                user.setRole(User.Role.ADMIN);
            } else {
                // Regular user registration
                user.setRole(User.Role.USER);
            }
            
            user.setActive(true);

            User registeredUser = userService.registerUser(user, passwordEncoder);
            
            final UserDetails userDetails = userService.loadUserByUsername(registeredUser.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            response.put("token", jwt);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);
            final User user = userService.findByEmail(loginRequest.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            response.put("token", jwt);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
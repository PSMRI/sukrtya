package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.AuthResponse;
import com.piramal.sukrtya.DTO.ChangePasswordRequestDTO;
import com.piramal.sukrtya.DTO.UserCredentials;
import com.piramal.sukrtya.DTO.UserDTO;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.security.JwtUtil;
import com.piramal.sukrtya.services.ChangePasswordService;
import com.piramal.sukrtya.services.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("sukrtya/api")
public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final LoginService loginService;
    private final JwtUtil jwtUtil; // Utility class for JWT operations
    private final ChangePasswordService changePasswordService;

    public LoginController(LoginService loginService, JwtUtil jwtUtil, ChangePasswordService changePasswordService) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
        this.changePasswordService = changePasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@RequestBody UserCredentials loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUserName());

        UserDTO userData = loginService.getUserData(loginRequest.getUserName(), loginRequest.getPassword());
        if (userData != null) {
            // Generate JWT Token
            String token = jwtUtil.generateToken(userData.getUserName(), Map.of("role", "ADMIN"));
            logger.info("Login successful for user: {}", loginRequest.getUserName());

            // Return the token along with user data
            return ResponseEntity.ok(new AuthResponse(userData, token));
        } else {
            logger.warn("Invalid login credentials for user: {}", loginRequest.getUserName());

            ApiResponse<String> errorResponse = new ApiResponse<>("error", "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request,
                                            @RequestHeader("Authorization") String authHeader) {

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7); // Extract the token part by removing "Bearer "

            // Extract username from JWT token
            String username = jwtUtil.extractUsername(jwtToken);

            // Validate new password and confirmation
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("New password and confirmation do not match");
            }

            // Attempt to change the password
            boolean isPasswordChanged = changePasswordService.changePassword(username, request.getOldPassword(), request.getNewPassword());

            if (isPasswordChanged) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Old password is incorrect");
            }

        } else {
            // If the Authorization header is missing or malformed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or malformed");
        }
    }
    @GetMapping("/test/cors")
    public String testCors() {
        return "CORS is working!";
    }

}

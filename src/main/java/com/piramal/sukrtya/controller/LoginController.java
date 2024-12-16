package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.AuthResponse;
import com.piramal.sukrtya.DTO.UserCredentials;
import com.piramal.sukrtya.DTO.UserDTO;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.security.JwtUtil;
import com.piramal.sukrtya.services.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final LoginService loginService;
    private final JwtUtil jwtUtil; // Utility class for JWT operations

    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/postLogin")
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

}

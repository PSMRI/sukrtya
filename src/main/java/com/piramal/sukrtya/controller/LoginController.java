package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.AuthResponse;
import com.piramal.sukrtya.DTO.ChangePasswordRequest;
import com.piramal.sukrtya.DTO.UserCredentials;
import com.piramal.sukrtya.DTO.UserDTO;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.exceptions.handler.UnauthorizedException;
import com.piramal.sukrtya.security.JwtUtil;
import com.piramal.sukrtya.services.ChangePasswordService;
import com.piramal.sukrtya.services.LoginService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("sukrtya/api")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody ChangePasswordRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {
        logger.info("Password change request initiated.");
        ApiResponse<?> response = changePasswordService.validateAndChangePassword(authHeader, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/test/cors")
    public String testCors() {
        throw new UnauthorizedException("Unauthorized access: Invalid token");
    }

}

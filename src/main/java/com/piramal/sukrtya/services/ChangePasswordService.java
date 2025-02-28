package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.ChangePasswordRequest;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.exceptions.handler.BadRequestException;
import com.piramal.sukrtya.exceptions.handler.UnauthorizedException;
import com.piramal.sukrtya.repository.ChangePasswordRepository;
import com.piramal.sukrtya.security.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private static final Logger logger = LogManager.getLogger(ChangePasswordService.class);

    private final ChangePasswordRepository changePasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ChangePasswordService(ChangePasswordRepository changePasswordRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.changePasswordRepository = changePasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse<?> validateAndChangePassword(String authHeader, ChangePasswordRequest request) {
        logger.info("Starting password change validation process.");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Authorization token is missing or malformed.");
            throw new UnauthorizedException("Authorization token is missing or malformed");
        }

        String jwtToken = authHeader.substring(7); // Extract the token part by removing "Bearer "
        String username = jwtUtil.extractUsername(jwtToken);
        logger.info("Extracted username from JWT token: {}", username);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            logger.error("New password and confirm password do not match.");
            throw new BadRequestException("New password and confirm password do not match");
        }

        boolean isPasswordChanged = changePassword(username, request.getOldPassword(), request.getNewPassword());

        if (!isPasswordChanged) {
            logger.error("Old password is incorrect for user: {}", username);
            throw new BadRequestException("Old password is incorrect");
        }

        logger.info("Password changed successfully for user: {}", username);
        return new ApiResponse<>("success", "Password changed successfully", null);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for user: {}", username);

        String currentPasswordHash = changePasswordRepository.findPasswordByUsername(username);
        logger.debug("Current password hash for user {}: {}", username, currentPasswordHash);

        // Check if the old password matches the stored password
        if (currentPasswordHash != null && oldPassword.equals(currentPasswordHash)) {
            logger.info("Old password is correct. Updating to new password.");
            String hashedNewPassword = passwordEncoder.encode(newPassword);
            int rowsUpdated = changePasswordRepository.updatePassword(username, newPassword);
            if (rowsUpdated > 0) {
                logger.info("Password update successful for user: {}", username);
                return true;
            } else {
                logger.error("Failed to update password for user: {}", username);
            }
        } else {
            logger.error("Old password does not match for user: {}", username);
        }

        return false;
    }



    public ApiResponse<?> getProfile(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization token is missing or malformed");
        }
        String jwtToken = authHeader.substring(7); // Extract the token part by removing "Bearer "
        String username = jwtUtil.extractUsername(jwtToken);

        return new ApiResponse<>("success", username, null);
    }

}

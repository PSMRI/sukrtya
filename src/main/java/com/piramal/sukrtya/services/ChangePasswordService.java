package com.piramal.sukrtya.services;

import com.piramal.sukrtya.repository.ChangePasswordRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final ChangePasswordRepository changePasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(ChangePasswordRepository changePasswordRepository, PasswordEncoder passwordEncoder) {
        this.changePasswordRepository = changePasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // Retrieve the current password hash from the database
        String currentPasswordHash = changePasswordRepository.findPasswordByUsername(username);

        if (currentPasswordHash != null && passwordEncoder.matches(oldPassword, currentPasswordHash)) {
            // Hash the new password
            String hashedNewPassword = passwordEncoder.encode(newPassword);

            // Update the password in the database
            int rowsUpdated = changePasswordRepository.updatePassword(username, hashedNewPassword);

            // Return true if the update was successful
            return rowsUpdated > 0;
        }

        // Return false if the old password does not match
        return false;
    }
}



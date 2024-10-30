package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.models.UserProfile;
import com.piramal.spring_boot_demo.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // Endpoint to get all user profiles
    @GetMapping
    public List<UserProfile> getAllUserProfiles() {
        return userProfileService.getAllUserProfiles();
    }

    // Endpoint to get a single user profile by ID (with associated forms)
    @GetMapping("/{id}")
    public Optional<UserProfile> getUserProfileById(@PathVariable Long id) {
        return userProfileService.getUserProfileById(id);
    }
}
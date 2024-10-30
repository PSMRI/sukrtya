package com.piramal.spring_boot_demo.services;
import com.piramal.spring_boot_demo.models.UserProfile;
import com.piramal.spring_boot_demo.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    // Fetch all user profiles
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    // Fetch a user profile by ID (including their forms)
    public Optional<UserProfile> getUserProfileById(Long profileID) {
        return userProfileRepository.findById(profileID);
    }
}

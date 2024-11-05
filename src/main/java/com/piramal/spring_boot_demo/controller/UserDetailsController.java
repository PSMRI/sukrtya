package com.piramal.spring_boot_demo.controller;
import com.piramal.spring_boot_demo.DTO.UserResponseDTO;
import com.piramal.spring_boot_demo.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDetailsController {

    @Autowired
    private UserDetailsService userProfileService;

    @GetMapping("/api/user/profile")
    public UserResponseDTO getUserProfileWithFacilities(
            @RequestParam String userName,
            @RequestParam String password) {
        return userProfileService.getUserProfileWithFacilities(userName, password);
    }
}

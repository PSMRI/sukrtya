package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.models.User;
import com.piramal.spring_boot_demo.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginDetails) {
        String userName = loginDetails.get("userName");
        String password = loginDetails.get("password");

        logger.info("Login attempt for user: {}", userName);

        Optional<User> userOpt = userService.login(userName, password);

        Map<String, Object> response = new HashMap<>();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("UserName", user.getUserName());
            response.put("Password", null); // Never return password in response
            response.put("userTypeID", user.getUserTypeID());
            response.put("profileID", user.getProfileID());
            response.put("userID", user.getUserID());
            response.put("profileName", user.getProfileName());
            response.put("profilePhoto", user.getProfilePhoto());
            response.put("ApprovalStatus", user.getApprovalStatus());
            response.put("userType", user.getUserType());
        } else {
            logger.warn("Invalid login for user: {}", userName);
            response.put("UserName", null);
            response.put("Password", null);
            response.put("userTypeID", 0);
            response.put("profileID", 0);
            response.put("userID", 0);
            response.put("profileName", null);
            response.put("profilePhoto", null);
            response.put("ApprovalStatus", 0);
            response.put("userType", null);
        }

        return ResponseEntity.ok(response);
    }
}

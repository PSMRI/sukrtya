package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.DTO.UserCredentials;
import com.piramal.spring_boot_demo.DTO.UserDTO;
import com.piramal.spring_boot_demo.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

@RestController
@RequestMapping("/api")
public class PostLoginController {

    @Autowired
    private LoginService userService;

    @PostMapping("/postLogin")
    public ResponseEntity<String> postLogin(@RequestBody UserCredentials loginRequest) {
        UserDTO userData = userService.getUserData(loginRequest.getUserName(), loginRequest.getPassword());

        // Convert UserDTO to JSON using Gson
        Gson gson = new Gson();

        if (userData != null) {
            String jsonResponse = gson.toJson(userData); // Convert UserDTO to JSON using Gson
            return ResponseEntity.ok(jsonResponse);
        } else {
            return ResponseEntity.status(401).body("{\"error\":\"Invalid credentials\"}");
        }
    }
}
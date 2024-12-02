package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.UserCredentials;
import com.piramal.sukrtya.DTO.UserDTO;
import com.piramal.sukrtya.exceptions.handler.ErrorResponse;
import com.piramal.sukrtya.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/postLogin")
    public ResponseEntity<?> postLogin(@RequestBody UserCredentials loginRequest) {
        UserDTO userData = loginService.getUserData(loginRequest.getUserName(), loginRequest.getPassword());
        if (userData != null) {
                return ResponseEntity.ok(userData);
        } else {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid credentials"));
        }
    }
}
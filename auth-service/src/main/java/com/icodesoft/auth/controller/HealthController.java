package com.icodesoft.auth.controller;

import com.icodesoft.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private UserService loginUserService;

    @GetMapping("/health")
    public String ping() {
        return "Welcome " + this.loginUserService.getUserByName("gary").getUsername() + " to Auth Service!";
    }
}

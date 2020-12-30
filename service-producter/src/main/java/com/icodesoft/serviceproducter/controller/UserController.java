package com.icodesoft.serviceproducter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{id}")
    public String getUserNameById(@PathVariable("id") String id) {
        return "name: gary, id: " + id;
    }

    @GetMapping("/test")
    public String testFallback() throws InterruptedException {
        Thread.sleep(3000);
        logger.info("Service Name: service-product");
        return "Service Name: service-product";
    }
}

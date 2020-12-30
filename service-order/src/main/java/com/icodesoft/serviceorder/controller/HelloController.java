package com.icodesoft.serviceorder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 动态刷新配置内容
@RefreshScope
@RestController
@RequestMapping("/hello")
public class HelloController {
    @Value("${info.profile}")
    private String profile;

    @Value("${info.author}")
    private String author;

    @GetMapping("/config")
    public String getProfile() {
        return "Get value from github repository- key[info.profile]: " + this.profile;
    }

    @GetMapping("/author")
    public String getAuthor() {
        return "Get value from github repository- key[info.author]: " + this.author;
    }
}

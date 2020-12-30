package com.icodesoft.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class FallbackController {

    @GetMapping("/fallbackA")
    public Response fallbackA() {
        return Response.status(100).entity("服务暂时不可用").build();
    }
}

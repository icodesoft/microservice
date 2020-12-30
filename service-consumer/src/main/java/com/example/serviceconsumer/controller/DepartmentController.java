package com.example.serviceconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("restTemplateWithLB")
    private RestTemplate restTemplateWithLB;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/consumer")
    public String getUser() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-producter");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/user/123";
        return restTemplate.getForObject(url, String.class);
    }

    @GetMapping("/consumer/{id}")
    public String getUserInfo(@PathVariable("id") String id) {
        String url = "http://service-producter/user/" + id;
        return restTemplateWithLB.getForObject(url, String.class);
    }
}

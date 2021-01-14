package com.icodesoft.rabbitmq;

import com.icodesoft.rabbitmq.service.RabbitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceRabbitmqApplicationTests {

    @Autowired
    private RabbitService rabbitService;

    @Test
    void contextLoads() {
        this.rabbitService.sendMessage();
        this.rabbitService.sendMessages();
    }

}

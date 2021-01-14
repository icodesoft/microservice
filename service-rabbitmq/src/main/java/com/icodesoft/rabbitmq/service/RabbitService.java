package com.icodesoft.rabbitmq.service;

import com.icodesoft.rabbitmq.model.User;
import com.icodesoft.rabbitmq.utils.JacksonUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {
        String message = "hello rabbit!";
        System.out.println("send: " + message);
        this.rabbitTemplate.convertAndSend("hello", message);
    }

    public void sendMessage() {
        User user = new User(0, "gary", "Message");
        System.out.println("send Message: " + user);
        this.rabbitTemplate.convertAndSend("topic.message.test", user);
    }

    public void sendMessages() {
        User user = new User(1, "gary", "MessageList");
        System.out.println("send Messages: " + user);
        // 消息不管是否投递到交换机都进行ConfirmCallback回调，投递成功ack=true，否则为false
        this.rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            System.out.println("ack: " + ack);
            if (!ack) System.out.println("异常信息");
        }));

        // 交换机匹配到队列成功则不进行ReturnCallback回调，否则先进行ReturnCallback回调再进行ConfirmCallback回调
        // 如果消息成功投递到交换机，但没匹配到队列，则ConfirmCallback回调ack仍为true
        this.rabbitTemplate.setReturnCallback(((message, replyCode, replyTest, exchange, routingKey) -> {
            System.out.println("*****************return callback**************************");
        }));
        this.rabbitTemplate.convertAndSend("topic.messages.test.gg", user);
    }

    @RabbitHandler
    @RabbitListener(queues = "hello")
    public void receive(String message) {
        System.out.println("receive hello: " + message);
    }

    @RabbitHandler
    @RabbitListener(queues = "topic.message.test")
    public void receiveMessage(User user) {
        System.out.println("receive message: " + user);
    }

    @RabbitHandler
    @RabbitListener(queues = "topic.messages.test")
    public void receiveMessages(Message message, Channel channel) throws IOException {
        System.out.println("receive messages: " + Arrays.toString(message.getBody()));
        User user = JacksonUtil.json2Bean(Arrays.toString(message.getBody()), User.class);
        System.out.println("receive user: " + user);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

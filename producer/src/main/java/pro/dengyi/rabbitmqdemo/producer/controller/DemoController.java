package pro.dengyi.rabbitmqdemo.producer.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("message")
public class DemoController {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/sendMessage")
    public Map<String, Object> sendMessage() {
        Map<String, Object> map = new HashMap<>();
        rabbitTemplate.convertAndSend("directExchangeName", "routkey", "hello");
        map.put("status", true);
        map.put("message", "成功");
        return map;
    }

    @GetMapping("/sendError")
    public Map<String, Object> sendError() {
        Map<String, Object> map = new HashMap<>();
        rabbitTemplate.convertAndSend("directExchangeName1", "directQueueName", "hello");
        map.put("status", true);
        map.put("message", "成功");
        return map;
    }
}

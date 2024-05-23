package edu.hm.peslalz.thesis.notificationservice.client;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue notifications() {
        return new Queue("notifications");
    }
}

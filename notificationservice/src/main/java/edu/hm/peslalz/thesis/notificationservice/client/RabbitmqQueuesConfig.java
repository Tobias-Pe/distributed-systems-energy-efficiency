package edu.hm.peslalz.thesis.notificationservice.client;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue notificationsQueue() {
        return new Queue("notifications");
    }

    @Bean
    public Binding bindingNotificationsQueueOnPost(DirectExchange directExchange,
                                                   Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(directExchange).with("post");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("postservice.direct");
    }
}

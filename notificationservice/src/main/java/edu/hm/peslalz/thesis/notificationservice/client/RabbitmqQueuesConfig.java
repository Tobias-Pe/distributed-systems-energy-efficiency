package edu.hm.peslalz.thesis.notificationservice.client;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue notifications() {
        return new Queue("notifications");
    }

    @Bean
    public Binding binding(FanoutExchange fanout,
                            Queue notifications) {
        return BindingBuilder.bind(notifications).to(fanout);
    }

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("postservice.posts.fanout");
    }
}

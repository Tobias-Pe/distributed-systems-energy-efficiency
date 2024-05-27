package edu.hm.peslalz.thesis.postservice.client;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue postStatisticsQueue() {
        return new Queue("post-statistics");
    }

    @Bean
    public Queue postActionStatisticsQueue() {
        return new Queue("post-action-statistics");
    }

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
    public Binding bindingStatisticsQueueOnPost(DirectExchange directExchange,
                                                Queue postStatisticsQueue) {
        return BindingBuilder.bind(postStatisticsQueue).to(directExchange).with("post");
    }

    @Bean
    public Binding bindingStatisticsQueueOnLike(DirectExchange directExchange,
                                                Queue postActionStatisticsQueue) {
        return BindingBuilder.bind(postActionStatisticsQueue).to(directExchange).with("like");
    }

    @Bean
    public Binding bindingStatisticsQueueOnComment(DirectExchange directExchange,
                                                   Queue postActionStatisticsQueue) {
        return BindingBuilder.bind(postActionStatisticsQueue).to(directExchange).with("comment");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("postservice.direct");
    }
}

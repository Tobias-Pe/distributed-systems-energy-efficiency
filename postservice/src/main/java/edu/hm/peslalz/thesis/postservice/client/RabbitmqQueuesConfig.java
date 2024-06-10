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
    public Queue postFeedQueue() {
        return new Queue("post-feed");
    }

    @Bean
    public Queue postActionFeedQueue() {
        return new Queue("post-action-feed");
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
    public Binding bindingFeedQueueOnPost(DirectExchange directExchange,
                                                Queue postFeedQueue) {
        return BindingBuilder.bind(postFeedQueue).to(directExchange).with("post");
    }

    @Bean
    public Binding bindingFeedQueueOnLike(DirectExchange directExchange,
                                                Queue postActionFeedQueue) {
        return BindingBuilder.bind(postActionFeedQueue).to(directExchange).with("like");
    }

    @Bean
    public Binding bindingFeedQueueOnComment(DirectExchange directExchange,
                                                   Queue postActionFeedQueue) {
        return BindingBuilder.bind(postActionFeedQueue).to(directExchange).with("comment");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("postservice.direct");
    }
}

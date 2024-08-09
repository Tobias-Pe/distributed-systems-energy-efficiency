package edu.hm.peslalz.thesis.postservice.client;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
    public Binding bindingNotificationsQueueOnPost(DirectExchange postserviceDirectExchange,
                                                   Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(postserviceDirectExchange).with("post");
    }

    @Bean
    public Binding bindingStatisticsQueueOnPost(DirectExchange postserviceDirectExchange,
                                                Queue postStatisticsQueue) {
        return BindingBuilder.bind(postStatisticsQueue).to(postserviceDirectExchange).with("post");
    }

    @Bean
    public Binding bindingStatisticsQueueOnLike(DirectExchange postserviceDirectExchange,
                                                Queue postActionStatisticsQueue) {
        return BindingBuilder.bind(postActionStatisticsQueue).to(postserviceDirectExchange).with("like");
    }

    @Bean
    public Binding bindingStatisticsQueueOnComment(DirectExchange postserviceDirectExchange,
                                                   Queue postActionStatisticsQueue) {
        return BindingBuilder.bind(postActionStatisticsQueue).to(postserviceDirectExchange).with("comment");
    }

    @Bean
    public Binding bindingFeedQueueOnPost(DirectExchange postserviceDirectExchange,
                                          Queue postFeedQueue) {
        return BindingBuilder.bind(postFeedQueue).to(postserviceDirectExchange).with("post");
    }

    @Bean
    public Binding bindingFeedQueueOnLike(DirectExchange postserviceDirectExchange,
                                          Queue postActionFeedQueue) {
        return BindingBuilder.bind(postActionFeedQueue).to(postserviceDirectExchange).with("like");
    }

    @Bean
    public Binding bindingFeedQueueOnComment(DirectExchange postserviceDirectExchange,
                                             Queue postActionFeedQueue) {
        return BindingBuilder.bind(postActionFeedQueue).to(postserviceDirectExchange).with("comment");
    }

    @Bean
    public DirectExchange postserviceDirectExchange() {
        return new DirectExchange("postservice.direct");
    }

    @Bean
    public DirectExchange userserviceRpcExchange() {
        return new DirectExchange("userservice.rpc");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.lang.*"));
        return converter;
    }
}

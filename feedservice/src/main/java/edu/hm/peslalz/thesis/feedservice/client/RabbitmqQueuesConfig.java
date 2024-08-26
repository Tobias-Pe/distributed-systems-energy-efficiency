package edu.hm.peslalz.thesis.feedservice.client;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue postFeedQueue() {
        return new Queue("post-feed");
    }

    @Bean
    public Queue postActionFeedQueue() {
        return new Queue("post-action-feed");
    }

    @Bean
    public DirectExchange postExchange() {
        return new DirectExchange("postservice.rpc");
    }

    @Bean
    public DirectExchange trendExchange() {
        return new DirectExchange("statisticservice.rpc");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.*"));
        return converter;
    }
}

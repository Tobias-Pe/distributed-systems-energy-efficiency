package edu.hm.peslalz.thesis.feedservice.client;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}

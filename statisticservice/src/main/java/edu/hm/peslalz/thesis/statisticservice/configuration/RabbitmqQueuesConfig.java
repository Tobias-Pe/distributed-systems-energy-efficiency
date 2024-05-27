package edu.hm.peslalz.thesis.statisticservice.configuration;

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
    public Queue userStatisticsQueue() {
        return new Queue("user-statistics");
    }
}

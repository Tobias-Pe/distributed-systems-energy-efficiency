package edu.hm.peslalz.thesis.userservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqQueuesConfig {
    @Bean
    public Queue statisticsQueue() {
        return new Queue("user-statistics");
    }

    @Bean
    public Binding bindingStatisticsQueue(FanoutExchange fanout,
                                          Queue statisticsQueue) {
        return BindingBuilder.bind(statisticsQueue).to(fanout);
    }

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("userservice.subscription.fanout");
    }
}

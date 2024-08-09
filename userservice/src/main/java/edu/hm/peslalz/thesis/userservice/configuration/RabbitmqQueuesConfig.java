package edu.hm.peslalz.thesis.userservice.configuration;

import org.springframework.amqp.core.*;
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

    @Bean
    public Queue userVerficiationRpcQueue() {
        return new Queue("userservice.rpc.verification");
    }

    @Bean
    public DirectExchange userserviceRpcExchange() {
        return new DirectExchange("userservice.rpc");
    }

    @Bean
    public Binding binding(DirectExchange userserviceRpcExchange,
                           Queue userVerficiationRpcQueue) {
        return BindingBuilder.bind(userVerficiationRpcQueue)
                .to(userserviceRpcExchange)
                .with("rpc");
    }
}

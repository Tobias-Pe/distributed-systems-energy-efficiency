package edu.hm.peslalz.thesis.statisticservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Queue userStatisticsQueue() {
        return new Queue("user-statistics");
    }

    @Bean
    public DirectExchange trendExchange() {
        return new DirectExchange("statisticservice.rpc");
    }

    @Bean
    public Queue postsRpcQueue() {
        return new Queue("statisticservice.rpc.rpc-posts");
    }

    @Bean
    public Queue categoriesRpcQueue() {
        return new Queue("statisticservice.rpc.rpc-categories");
    }

    @Bean
    public Queue usersRpcQueue() {
        return new Queue("statisticservice.rpc.rpc-users");
    }

    @Bean
    public Binding bindingPostsToRpcExchange(DirectExchange trendExchange,
                                                  Queue postsRpcQueue) {
        return BindingBuilder.bind(postsRpcQueue)
                .to(trendExchange)
                .with("rpc-posts");
    }

    @Bean
    public Binding bindingCategoriesToRpcExchange(DirectExchange trendExchange,
                                                  Queue categoriesRpcQueue) {
        return BindingBuilder.bind(categoriesRpcQueue)
                .to(trendExchange)
                .with("rpc-categories");
    }

    @Bean
    public Binding bindingUsersToRpcExchange(DirectExchange trendExchange,
                                                  Queue usersRpcQueue) {
        return BindingBuilder.bind(usersRpcQueue)
                .to(trendExchange)
                .with("rpc-users");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.*", "org.springframework.data.domain.*"));
        return converter;
    }

    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public SimpleRabbitListenerContainerFactory consumerBatchContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setBatchListener(true); // configures a BatchMessageListenerAdapter
        factory.setBatchSize(10);
        factory.setConsumerBatchEnabled(true);
        return factory;
    }
}

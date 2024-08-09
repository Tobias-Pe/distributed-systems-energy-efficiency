package edu.hm.peslalz.thesis.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RpcService {
    UserAccountService userAccountService;
    private final Counter verificationCounter;

    public RpcService(UserAccountService userAccountService, MeterRegistry registry) {
        this.userAccountService = userAccountService;
        this.verificationCounter = Counter.builder("userservice_verifications_counter")
                .description("Count of how often id's were checked for existence")
                .register(registry);
    }

    // enable tracing for rabbitmq listener
    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true);
    }

    @RabbitListener(queues = "userservice.rpc.verification", concurrency = "1-5")
    public boolean verify(Integer userId) {
        log.info("verifying exist user {}", userId);
        boolean result = userAccountService.existUserWithId(userId);
        verificationCounter.increment();
        return result;
    }
}

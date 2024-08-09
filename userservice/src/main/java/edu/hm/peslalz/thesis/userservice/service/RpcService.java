package edu.hm.peslalz.thesis.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.userservice.entity.FollowerDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@Log4j2
public class RpcService {
    UserAccountService userAccountService;
    private final Counter verificationCounter;
    private final Counter getFollowersCounter;

    public RpcService(UserAccountService userAccountService, MeterRegistry registry) {
        this.userAccountService = userAccountService;
        this.verificationCounter = Counter.builder("userservice_verifications_counter")
                .description("Count of how often id's were checked for existence")
                .register(registry);
        this.getFollowersCounter = Counter.builder("userservice_followers_counter")
                .description("Count of how often the followers were queried")
                .register(registry);
    }

    // enable tracing for rabbitmq listener
    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true);
    }

    @RabbitListener(queues = "userservice.rpc.verification", concurrency = "1-5")
    public boolean verify(Integer userId) {
        log.info("[rpc] verifying exist user {}", userId);
        boolean result = userAccountService.existUserWithId(userId);
        verificationCounter.increment();
        return result;
    }

    @RabbitListener(queues = "userservice.rpc.followers", concurrency = "1-5")
    public String getFollowers(Integer userId) {
        log.info("[rpc] getting follower for user {}", userId);
        Set<FollowerDto> result = userAccountService.getFollowers(userId);

        ObjectMapper mapper = new ObjectMapper();
        String message;
        try {
            message = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        getFollowersCounter.increment();
        return message;
    }
}

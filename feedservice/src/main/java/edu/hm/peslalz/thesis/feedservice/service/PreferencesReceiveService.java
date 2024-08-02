package edu.hm.peslalz.thesis.feedservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.feedservice.entity.PostActionMessage;
import edu.hm.peslalz.thesis.feedservice.entity.PostMessage;
import edu.hm.peslalz.thesis.feedservice.entity.UserPreference;
import edu.hm.peslalz.thesis.feedservice.repository.UserPreferenceRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Log4j2
@EnableRetry
public class PreferencesReceiveService {
    UserPreferenceRepository userPreferenceRepository;

    private final Counter preferenceUpdateCounter;

    public PreferencesReceiveService(UserPreferenceRepository userPreferenceRepository, MeterRegistry registry) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.preferenceUpdateCounter = Counter.builder("feedservice_updated_preferences_counter")
                .description("Count of preference updates")
                .register(registry);
    }

    // enable tracing for rabbitmq listener
    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true);
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 1.5)
    )
    @RabbitListener(queues = "post-feed", concurrency = "2-4")
    public void receivePost(String post) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PostMessage postMessage = mapper.readValue(post, PostMessage.class);
        log.info("post {} received from {}", postMessage.getId(), postMessage.getUserId());
        applyEvent(postMessage, postMessage.getUserId());
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 1.5)
    )
    @RabbitListener(queues = "post-action-feed", concurrency = "2-4")
    public void receivePostAction(String postAction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PostActionMessage postActionMessage = mapper.readValue(postAction, PostActionMessage.class);
        log.info("post-action {} received from {}", postActionMessage.getPostMessage().getId(), postActionMessage.getUserId());
        applyEvent(postActionMessage.getPostMessage(), postActionMessage.getUserId());
    }

    private void applyEvent(PostMessage postMessage, Integer userId) {
        UserPreference userPreference = userPreferenceRepository.findByUserId(userId).orElse(new UserPreference(userId));
        // Ignore interactions for posts made by the user themselves
        if (!Objects.equals(userId, postMessage.getUserId())) {
            userPreference.addUserInteraction(postMessage.getUserId());
        }
        postMessage.getCategories().forEach(userPreference::addCategoryInteraction);
        userPreferenceRepository.save(userPreference);
        preferenceUpdateCounter.increment();
    }
}

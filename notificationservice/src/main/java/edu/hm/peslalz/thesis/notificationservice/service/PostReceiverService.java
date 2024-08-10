package edu.hm.peslalz.thesis.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.notificationservice.entity.PostMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class PostReceiverService {

    NotificationService notificationService;

    public PostReceiverService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // enable tracing for rabbitmq listener
    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true);
    }

    @Transactional
    @RabbitListener(queues = "notifications", concurrency = "2-8")
    public void receive(String post) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PostMessage postMessage = mapper.readValue(post, PostMessage.class);
        log.info("received post {} from {}", postMessage.getId(), postMessage.getUserId());
        notificationService.notifySubscriber(postMessage);
    }
}

package edu.hm.peslalz.thesis.statisticservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.statisticservice.entity.PostActionMessage;
import edu.hm.peslalz.thesis.statisticservice.entity.PostMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class StatisticsReceiveService {
    TrendService trendService;

    public StatisticsReceiveService(TrendService trendService) {
        this.trendService = trendService;
    }

    // enable tracing for rabbitmq listener
    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true);
    }

    @Transactional
    @RabbitListener(queues = "post-statistics", concurrency = "2-4", containerFactory = "consumerBatchContainerFactory")
    public void receivePost(List<String> posts) throws JsonProcessingException {
        for (String post : posts) {
            ObjectMapper mapper = new ObjectMapper();
            PostMessage postMessage = mapper.readValue(post, PostMessage.class);
            log.info("post {} received from {}", postMessage.getId(), postMessage.getUserId());
            trendService.registerNewPost(postMessage);
        }
    }


    @Transactional
    @RabbitListener(queues = "post-action-statistics", concurrency = "2-4", containerFactory = "consumerBatchContainerFactory")
    public void receivePostAction(List<String> postActions) throws JsonProcessingException {
        for (String postAction : postActions) {
            ObjectMapper mapper = new ObjectMapper();
            PostActionMessage postActionMessage = mapper.readValue(postAction, PostActionMessage.class);
            if (postActionMessage.getAction() == null || postActionMessage.getAction().trim().isEmpty()) {
                log.error("Action null {}",postActionMessage);
                continue;
            }
            log.info("post-action {} with action '{}' received from {}", postActionMessage.getPostMessage().getId(),postActionMessage.getAction(), postActionMessage.getUserId());
            trendService.registerPostAction(postActionMessage);
        }
    }


    @Transactional
    @RabbitListener(queues = "user-statistics", concurrency = "2-4", containerFactory = "consumerBatchContainerFactory")
    public void receiveUser(List<Integer> users) {
        for (Integer user : users) {
            log.info("user {} has a new follower", user);
            trendService.registerAccountFollowed(user);
        }
    }
}

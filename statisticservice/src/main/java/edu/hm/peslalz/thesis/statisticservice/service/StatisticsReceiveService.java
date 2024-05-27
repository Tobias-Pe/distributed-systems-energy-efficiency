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

    @RabbitListener(queues = "post-statistics")
    public void receivePost(String post) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PostMessage postMessage = mapper.readValue(post, PostMessage.class);
        log.info("post {} received from {}", postMessage.getId(), postMessage.getUserId());
        trendService.registerNewPost(postMessage);
    }

    @RabbitListener(queues = "post-action-statistics")
    public void receivePostAction(String postAction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PostActionMessage postActionMessage = mapper.readValue(postAction, PostActionMessage.class);
        log.info("post-action {} received from {}", postActionMessage.getPostMessage().getId(), postActionMessage.getUserId());
        trendService.registerPostAction(postActionMessage);
    }

    @RabbitListener(queues = "user-statistics")
    public void receiveUser(String user) {
        Integer followedUser = Integer.valueOf(user);
        log.info("user {} has a new follower", followedUser);
        trendService.registerAccountFollowed(followedUser);
    }
}

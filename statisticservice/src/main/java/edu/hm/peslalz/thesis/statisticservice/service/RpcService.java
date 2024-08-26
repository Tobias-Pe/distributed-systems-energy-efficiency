package edu.hm.peslalz.thesis.statisticservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class RpcService {
    TrendService trendService;
    private final Counter categoriesCounter;
    private final Counter postsCounter;
    private final Counter usersCounter;

    public RpcService(TrendService trendService, MeterRegistry registry) {
        this.trendService = trendService;
        this.categoriesCounter = Counter.builder("statisticservice_categories_counter")
                .description("Count of how often a category trend was requested")
                .register(registry);
        this.postsCounter = Counter.builder("statisticservice_posts_counter")
                .description("Count of how often a post trend was requested")
                .register(registry);
        this.usersCounter = Counter.builder("statisticservice_users_counter")
                .description("Count of how often a user trend was requested")
                .register(registry);
    }

    @RabbitListener(queues = "statisticservice.rpc.rpc-categories", concurrency = "1-5")
    public String getTrendingCategories(Integer page) {
        log.info("[rpc] query for category trends page {}", page);

        Page<TrendInterface> result = trendService.getCategoryTrends(page);
        String messageOut;
        ObjectMapper mapper = new ObjectMapper();
        try {
            messageOut = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        postsCounter.increment();
        return messageOut;
    }

    @RabbitListener(queues = "statisticservice.rpc.rpc-posts", concurrency = "1-5")
    public String getTrendingPosts(Integer page) {
        log.info("[rpc] query for post trends {}", page);

        Page<TrendInterface> result = trendService.getPostTrends(page);
        String messageOut;
        ObjectMapper mapper = new ObjectMapper();
        try {
            messageOut = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        postsCounter.increment();
        return messageOut;
    }

    @RabbitListener(queues = "statisticservice.rpc.rpc-users", concurrency = "1-5")
    public String getTrendingUsers(Integer page) {
        log.info("[rpc] query for user trends {}", page);

        Page<TrendInterface> result = trendService.getUserTrends(page);
        String messageOut;
        ObjectMapper mapper = new ObjectMapper();
        try {
            messageOut = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        postsCounter.increment();
        return messageOut;
    }
}

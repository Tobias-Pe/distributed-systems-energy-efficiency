package edu.hm.peslalz.thesis.feedservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.feedservice.entity.PagedTrendResponse;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class TrendClient {
    private final DirectExchange trendExchange;
    private final RabbitTemplate template;

    public TrendClient(DirectExchange trendExchange, RabbitTemplate template) {
        this.trendExchange = trendExchange;
        this.template = template;
    }

    @Cacheable("trends/categories")
    public PagedTrendResponse getTrendingCategories(int page) {
        return getPagedTrendResponse("rpc-categories", page);
    }

    @Cacheable("trends/posts")
    public PagedTrendResponse getTrendingPosts(int page){
        return getPagedTrendResponse("rpc-posts", page);
    }

    @Cacheable("trends/users")
    public PagedTrendResponse getTrendingUsers(int page){
        return getPagedTrendResponse("rpc-users", page);
    }

    private PagedTrendResponse getPagedTrendResponse(String routingKey, int page) {
        String response = (String) this.template.convertSendAndReceive(trendExchange.getName(), routingKey, page);
        ObjectMapper mapper = new ObjectMapper();
        PagedTrendResponse pagedTrendResponse;
        try {
            pagedTrendResponse = mapper.readValue(response, PagedTrendResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return pagedTrendResponse;
    }
}

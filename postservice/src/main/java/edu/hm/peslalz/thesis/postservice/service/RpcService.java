package edu.hm.peslalz.thesis.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import edu.hm.peslalz.thesis.postservice.entity.PostsRequestDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class RpcService {

    PostService postService;
    private final Counter postsCounter;
    private final Counter singlePostCounter;

    public RpcService(PostService postService, MeterRegistry registry) {
        this.postService = postService;
        this.postsCounter = Counter.builder("postservice_posts_counter")
                .description("Count of how often posts were requested")
                .register(registry);
        this.singlePostCounter = Counter.builder("postservice_post_counter")
                .description("Count of how often a single post was requested")
                .register(registry);
    }

    @RabbitListener(queues = "postservice.rpc.rpc-posts", concurrency = "1-5")
    public String getPosts(String message) {
        log.info("[rpc] query for multiple posts {}", message);

        ObjectMapper mapper = new ObjectMapper();
        PostsRequestDTO postsRequestDTO;
        try {
            postsRequestDTO = mapper.readValue(message, PostsRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Page<Post> result = postService.getPostsByCategoryUserId(postsRequestDTO.category(), postsRequestDTO.userId(), postsRequestDTO.page(), postsRequestDTO.size());
        String messageOut;
        try {
            messageOut = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        postsCounter.increment();
        return messageOut;
    }

    @RabbitListener(queues = "postservice.rpc.rpc-post", concurrency = "1-5")
    public String getPost(Integer id) {
        log.info("[rpc] query for post with id {}", id);
        Post result = postService.getPostById(id);

        ObjectMapper mapper = new ObjectMapper();
        String message;
        try {
            message = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        singlePostCounter.increment();
        return message;
    }
}

package edu.hm.peslalz.thesis.feedservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.feedservice.entity.PagedPostResponse;
import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import edu.hm.peslalz.thesis.feedservice.entity.PostsRequestDTO;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PostClient {
    private final DirectExchange postExchange;
    private final RabbitTemplate template;

    public PostClient(DirectExchange postExchange, RabbitTemplate template) {
        this.postExchange = postExchange;
        this.template = template;
    }

    @Cacheable("posts")
    public PagedPostResponse getPosts(String category, Integer userId, int page, int size) {
        PostsRequestDTO postsRequestDTO = new PostsRequestDTO(category, userId, page, size);
        ObjectMapper mapper = new ObjectMapper();
        String message;
        try {
            message = mapper.writeValueAsString(postsRequestDTO);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        String response = (String) this.template.convertSendAndReceive(postExchange.getName(), "rpc-posts", message);
        PagedPostResponse pagedPostResponse;
        try {
            pagedPostResponse = mapper.readValue(response, PagedPostResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return pagedPostResponse;
    }

    @Cacheable("post")
    public PostDTO getPost(int id) {
        String response = (String) this.template.convertSendAndReceive(postExchange.getName(), "rpc-post", id);
        ObjectMapper mapper = new ObjectMapper();
        PostDTO postDTO;
        try {
            postDTO = mapper.readValue(response, PostDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return postDTO;
    }
}

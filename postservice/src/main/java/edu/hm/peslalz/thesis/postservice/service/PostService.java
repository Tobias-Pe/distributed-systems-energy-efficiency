package edu.hm.peslalz.thesis.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.postservice.entity.*;
import edu.hm.peslalz.thesis.postservice.repository.CategoryRepository;
import edu.hm.peslalz.thesis.postservice.repository.CommentRepository;
import edu.hm.peslalz.thesis.postservice.repository.PostRepository;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@EnableRetry
@Service
public class PostService {
    PostRepository postRepository;
    CategoryRepository categoryRepository;
    CommentRepository commentRepository;

    private final RabbitTemplate template;
    private final DirectExchange postserviceDirectExchange;
    private final DirectExchange userserviceRpcExchange;

    @Autowired
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, CommentRepository commentRepository, RabbitTemplate template, DirectExchange postserviceDirectExchange, DirectExchange userserviceRpcExchange) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.template = template;
        // enable tracing for rabbitmq template
        this.template.setObservationEnabled(true);
        this.postserviceDirectExchange = postserviceDirectExchange;
        this.userserviceRpcExchange = userserviceRpcExchange;
    }

    Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<Post> getPostsByCategoryUserId(String category, Integer userId, int page, int size) {
        Page<Integer> postIDs = postRepository.findAllIDsByCategoryAndUserId(category, userId, PageRequest.of(page, size));
        List<Post> postList = postRepository.findAllById(postIDs.getContent());
        return new PageImpl<>(postList, postIDs.getPageable(), postIDs.getTotalElements());
    }

    public ImageData getPostImage(int id) {
        return postRepository.findByIdJoinImage(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getImageData();
    }

    public Post createPost(PostRequest postRequest, MultipartFile multipartFile) {
        checkUserExists(postRequest.getUserId());
        Post post = new Post(postRequest, multipartFile);
        categoryRepository.saveAll(post.getCategories());
        post = savePost(post);
        publish(new PostMessage(post), "post");
        return post;
    }

    private void publish(Object object, String routingKey) {
        ObjectMapper mapper = new ObjectMapper();
        String message;
        try {
            message = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        this.template.convertAndSend(postserviceDirectExchange.getName(), routingKey, message);
    }

    void checkUserExists(Integer userId) {
        Boolean response = (Boolean) this.template.convertSendAndReceive(userserviceRpcExchange.getName(), "rpc", userId);
        if (response == null || !response) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 400, maxDelay = 1000, multiplier = 1.33)
    )
    public Post likePost(int id, Integer userId) {
        Post post = this.postRepository.likePost(id).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exist");
        });
        publish(new PostActionMessage(userId, "like", new PostMessage(post)), "like");
        return post;
    }

    private Post lockAndGetPost(int id) {
        return postRepository.lockAndFindById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 400, maxDelay = 1000, multiplier = 1.33)
    )
    public Post commentPost(int id, CommentRequest commentRequest) {
        checkUserExists(commentRequest.getUserId());
        Post post = lockAndGetPost(id);
        Comment comment = new Comment(commentRequest);
        commentRepository.save(comment);
        post.getComments().add(comment);
        post = savePost(post);
        publish(new PostActionMessage(commentRequest.getUserId(), "comment", new PostMessage(post)), "comment");
        return post;
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 400, maxDelay = 1000, multiplier = 1.33)
    )
    public Comment likeComment(int id) {
        Comment comment = commentRepository.lockAndFindById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
        return comment;
    }
}

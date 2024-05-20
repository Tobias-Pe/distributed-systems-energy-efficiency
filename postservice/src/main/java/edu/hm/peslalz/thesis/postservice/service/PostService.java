package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.client.UserClient;
import edu.hm.peslalz.thesis.postservice.entity.*;
import edu.hm.peslalz.thesis.postservice.repository.CategoryRepository;
import edu.hm.peslalz.thesis.postservice.repository.CommentRepository;
import edu.hm.peslalz.thesis.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@EnableRetry
@Service
public class PostService {
    PostRepository postRepository;
    CategoryRepository categoryRepository;
    CommentRepository commentRepository;
    UserClient userClient;

    @Autowired
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, CommentRepository commentRepository, UserClient userClient) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.userClient = userClient;
    }

    Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Set<Post> getPostsByCategory(String category) {
        return categoryRepository.findById(category).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getPosts();
    }

    public ImageData getPostImage(int id) {
        return postRepository.findByIdJoinImage(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getImageData();
    }

    public Post createPost(PostRequest postRequest, MultipartFile multipartFile) {
        checkUserExists(postRequest.getUserId());
        Post post = new Post(postRequest, multipartFile);
        categoryRepository.saveAll(post.getCategories());
        return savePost(post);
    }

    void checkUserExists(Integer userId) {
        try {
            userClient.getUserAccount(userId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist", e);
        }
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 1.5)
    )
    public Post likePost(int id) {
        Post post = lockAndGetPost(id);
        post.setLikes(post.getLikes() + 1);
        return savePost(post);
    }

    private Post lockAndGetPost(int id) {
        return postRepository.lockAndFindById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 1.5)
    )
    public Post commentPost(int id, CommentRequest commentRequest) {
        checkUserExists(commentRequest.getUserId());
        Post post = lockAndGetPost(id);
        Comment comment = new Comment(commentRequest);
        commentRepository.save(comment);
        post.getComments().add(comment);
        return savePost(post);
    }

    @Transactional
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 1.5)
    )
    public Comment likeComment(int id) {
        Comment comment = commentRepository.lockAndFindById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
        return comment;
    }
}

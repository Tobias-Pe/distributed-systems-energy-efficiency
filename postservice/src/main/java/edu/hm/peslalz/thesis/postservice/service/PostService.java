package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.entity.Comment;
import edu.hm.peslalz.thesis.postservice.entity.CommentRequest;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import edu.hm.peslalz.thesis.postservice.entity.PostRequest;
import edu.hm.peslalz.thesis.postservice.repository.CategoryRepository;
import edu.hm.peslalz.thesis.postservice.repository.CommentRepository;
import edu.hm.peslalz.thesis.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@EnableRetry
@Service
public class PostService {
    PostRepository postRepository;
    CategoryRepository categoryRepository;
    CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Post createPost(PostRequest postRequest) {
        Post post = new Post(postRequest);
        categoryRepository.saveAll(post.getCategories());
        return savePost(post);
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
        Post post = lockAndGetPost(id);
        Comment comment = new Comment(commentRequest);
        commentRepository.save(comment);
        post.getComments().add(comment);
        return savePost(post);
    }

    public Set<Post> getPostsByCategory(String category) {
        return categoryRepository.findById(category).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getPosts();
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

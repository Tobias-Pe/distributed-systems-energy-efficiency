package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.entity.*;
import edu.hm.peslalz.thesis.postservice.repository.CatergoryRepository;
import edu.hm.peslalz.thesis.postservice.repository.CommentRepository;
import edu.hm.peslalz.thesis.postservice.repository.PostRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class PostService {
    PostRepository postRepository;
    CatergoryRepository catergoryRepository;
    CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CatergoryRepository catergoryRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.catergoryRepository = catergoryRepository;
        this.commentRepository = commentRepository;
    }

    Post savePost(Post post) {
        try {
            post = postRepository.save(post);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            } else {
                throw ex;
            }
        }
        return post;
    }

    public Post getPostById(int id) {
        return postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Post createPost(PostRequest postRequest) {
        return savePost(new Post(postRequest));
    }

    public Post likePost(int id) {
        Post post = getPostById(id);
        post.setLikes(post.getLikes() + 1);
        return savePost(post);
    }

    public Post commentPost(int id, CommentRequest commentRequest) {
        Post post = getPostById(id);
        post.getComments().add(new Comment(commentRequest));
        return savePost(post);
    }

    public Set<Post> getPostsByCatergory(String catergory) {
        return postRepository.findByCategories_NameLike(catergory);
    }

    public Comment likeComment(int id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
        return comment;
    }
}

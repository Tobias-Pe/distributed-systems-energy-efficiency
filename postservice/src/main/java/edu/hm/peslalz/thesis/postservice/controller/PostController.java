package edu.hm.peslalz.thesis.postservice.controller;

import edu.hm.peslalz.thesis.postservice.entity.Comment;
import edu.hm.peslalz.thesis.postservice.entity.CommentRequest;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import edu.hm.peslalz.thesis.postservice.entity.PostRequest;
import edu.hm.peslalz.thesis.postservice.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("posts")
public class PostController {
    PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @GetMapping
    public Set<Post> getPostsByCategory(@RequestParam String category) {
        return postService.getPostsByCatergory(category);
    }

    @PostMapping("/{id}/like")
    public Post likePost(@PathVariable int id) {
        return postService.likePost(id);
    }

    @PostMapping("/{id}/comments")
    public Post commentPost(@PathVariable int id, @RequestBody CommentRequest commentRequest) {
        return postService.commentPost(id, commentRequest);
    }


}

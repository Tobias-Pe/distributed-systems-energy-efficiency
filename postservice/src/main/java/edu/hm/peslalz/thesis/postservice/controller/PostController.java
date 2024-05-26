package edu.hm.peslalz.thesis.postservice.controller;

import edu.hm.peslalz.thesis.postservice.entity.CommentRequest;
import edu.hm.peslalz.thesis.postservice.entity.ImageData;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import edu.hm.peslalz.thesis.postservice.entity.PostRequest;
import edu.hm.peslalz.thesis.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Set;

@RestController
@RequestMapping("posts")
@Log4j2
public class PostController {
    PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(description = "Create a post")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@ModelAttribute PostRequest postRequest, @RequestPart(value = "image", required = false) MultipartFile file) {
        log.info("Creating a new post by user {}; Image is present: {}", postRequest.getUserId(), file == null);
        return postService.createPost(postRequest, file);
    }

    @Operation(description = "Get the information of a post")
    @GetMapping(value = "/{id}")
    public Post getPost(@PathVariable int id) {
        log.info("Getting post with id {}", id);
        return postService.getPostById(id);
    }

    @Operation(description = "Get the information of a post")
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<InputStreamResource> getPostImage(@PathVariable int id) {
        log.info("Getting image of post with id {}", id);
        ImageData image = postService.getPostImage(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getImageType())).body(new InputStreamResource(new ByteArrayInputStream(image.getImageBytes())));
    }

    @Operation(description = "Get all posts paged using filters")
    @GetMapping
    public Page<Post> getPosts(@RequestParam(required = false) String category, @RequestParam(required = false) Integer userId, @RequestParam(defaultValue = "0") int page) {
        log.info("Getting all posts filter: [category:{}; userId:{}; page:{}]", category, userId, page);
        return postService.getPostsByCategoryUserId(category, userId, page);
    }

    @Operation(description = "Like a post")
    @PostMapping("/{id}/like")
    public Post likePost(@PathVariable int id) {
        log.info("Liking post with id {}", id);
        return postService.likePost(id);
    }

    @Operation(description = "Leave a comment to a post")
    @PostMapping("/{id}/comments")
    public Post commentPost(@PathVariable int id, @RequestBody CommentRequest commentRequest) {
        log.info("User {} commenting post with id {}", commentRequest.getUserId(), id);
        return postService.commentPost(id, commentRequest);
    }
}

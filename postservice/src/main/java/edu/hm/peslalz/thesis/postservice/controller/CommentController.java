package edu.hm.peslalz.thesis.postservice.controller;

import edu.hm.peslalz.thesis.postservice.entity.Comment;
import edu.hm.peslalz.thesis.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comments")
public class CommentController {
    PostService postService;

    public CommentController(PostService postService) {
        this.postService = postService;
    }

    @Operation(description = "Like a comment")
    @PostMapping("/{comment_id}/like")
    public Comment likeComment(@PathVariable(name = "comment_id") int commentId) {
        return postService.likeComment(commentId);
    }
}

package edu.hm.peslalz.thesis.postservice.controller;

import edu.hm.peslalz.thesis.postservice.entity.Comment;
import edu.hm.peslalz.thesis.postservice.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentController {
    PostService postService;

    public CommentController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/{comment_id}/like")
    public Comment commentPost(@PathVariable(name = "comment_id") int commentId) {
        return postService.likeComment(commentId);
    }
}

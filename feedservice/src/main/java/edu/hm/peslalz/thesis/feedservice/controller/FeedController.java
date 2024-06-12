package edu.hm.peslalz.thesis.feedservice.controller;

import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import edu.hm.peslalz.thesis.feedservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feeds")
@Log4j2
public class FeedController {
    FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @Operation(description = "Get the information of a post")
    @GetMapping(value = "/{userId}")
    public Slice<PostDTO> getPersonalizedFeed(@PathVariable int userId, @RequestParam(defaultValue = "0") int page) {
        log.info("Getting personalized feed for user {}", userId);
        return feedService.getPersonalizedFeed(userId, page);
    }

}

package edu.hm.peslalz.thesis.feedservice.client;

import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("postservice")
public interface PostClient {
    @GetMapping("posts")
    Page<PostDTO> getPosts(@RequestParam(required = false) String category, @RequestParam(required = false) Integer userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size);

    @GetMapping(value = "posts/{id}")
    PostDTO getPost(@PathVariable int id);
}

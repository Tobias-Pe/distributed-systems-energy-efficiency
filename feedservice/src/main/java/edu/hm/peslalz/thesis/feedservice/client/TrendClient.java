package edu.hm.peslalz.thesis.feedservice.client;

import edu.hm.peslalz.thesis.feedservice.entity.Trend;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("statisticservice")
public interface TrendClient {
    @GetMapping("trends/categories")
    Page<Trend> getTrendingCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size);

    @GetMapping("trends/posts")
    Page<Trend> getTrendingPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size);

    @GetMapping("trends/users")
    Page<Trend> getTrendingUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size);
}

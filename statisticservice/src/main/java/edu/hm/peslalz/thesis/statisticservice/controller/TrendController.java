package edu.hm.peslalz.thesis.statisticservice.controller;

import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import edu.hm.peslalz.thesis.statisticservice.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("trends")
@Log4j2
public class TrendController {
    TrendService trendService;

    @Autowired
    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @Operation(description = "Get the current category trends")
    @GetMapping("/categories")
    public Callable<Page<TrendInterface>> getTrendingCategories(@RequestParam(defaultValue = "0") int page) {
        return () -> {
            log.info("Querying category trends | page {}", page);
            return trendService.getCategoryTrends(page);
        };
    }

    @Operation(description = "Get the current category trends")
    @GetMapping("/posts")
    public Callable<Page<TrendInterface>> getTrendingPosts(@RequestParam(defaultValue = "0") int page) {
        return () -> {
            log.info("Querying post trends | page {}", page);
            return trendService.getPostTrends(page);
        };
    }

    @Operation(description = "Get the current category trends")
    @GetMapping("/users")
    public Callable<Page<TrendInterface>> getTrendingUsers(@RequestParam(defaultValue = "0") int page) {
        return () -> {
            log.info("Querying user trends | page {}", page);
            return trendService.getUserTrends(page);
        };
    }
}

package edu.hm.peslalz.thesis.statisticservice.service;

import edu.hm.peslalz.thesis.statisticservice.entity.*;
import edu.hm.peslalz.thesis.statisticservice.repository.ActionProtocolRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.CategoryRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.PostRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.FollowedUserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TrendService {
    public static final int TREND_SIZE = 10;
    public static final int TREND_RELEVANT_TIMERANGE_MINUTES = 2;

    private final Counter trendUpdateCounter;

    CategoryRepository categoryRepository;
    FollowedUserRepository followedUserRepository;
    PostRepository postRepository;
    ActionProtocolRepository actionProtocolRepository;

    public TrendService(CategoryRepository categoryRepository, FollowedUserRepository followedUserRepository, PostRepository postRepository, ActionProtocolRepository actionProtocolRepository, MeterRegistry registry) {
        this.categoryRepository = categoryRepository;
        this.followedUserRepository = followedUserRepository;
        this.postRepository = postRepository;
        this.actionProtocolRepository = actionProtocolRepository;
        this.trendUpdateCounter = Counter.builder("trendservice_trend_updates_counter")
                .description("Count of how often trends are updated")
                .register(registry);
    }

    @Transactional
    public void registerAccountFollowed(Integer userId) {
        FollowedUser followedUser = followedUserRepository.findByUserId(userId).orElse(new FollowedUser(userId));
        followedUserRepository.save(followedUser);
        followedUser.getInteractions().add(new ActionProtocol("new follower"));
        trendUpdateCounter.increment();
    }

    @Transactional
    public void registerPostAction(PostActionMessage postActionMessage) {
        Post post = postRepository.findByPostId(postActionMessage.getPostMessage().getId()).orElse(new Post(postActionMessage.getPostMessage().getId()));
        postRepository.save(post);
        post.getInteractions().add(new ActionProtocol(postActionMessage.getAction()));
        trendUpdateCounter.increment();
    }

    @Transactional
    public void registerNewPost(PostMessage postMessage) {
        postMessage.getCategories().forEach(this::registerCategory);
        trendUpdateCounter.increment(postMessage.getCategories().size());
    }

    private void registerCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName).orElse(new Category(categoryName));
        categoryRepository.save(category);
        category.getInteractions().add(new ActionProtocol("new post"));
    }

    public Page<TrendInterface> getCategoryTrends(int page) {
        Date oldestRelevantActions = Date.from(LocalDateTime.now().minusMinutes(TREND_RELEVANT_TIMERANGE_MINUTES).atZone(ZoneId.systemDefault()).toInstant());
        return categoryRepository.findByMostInteractionsAfter(oldestRelevantActions, PageRequest.of(page, TREND_SIZE));
    }

    public Page<TrendInterface> getPostTrends(int page) {
        Date oldestRelevantActions = Date.from(LocalDateTime.now().minusMinutes(TREND_RELEVANT_TIMERANGE_MINUTES).atZone(ZoneId.systemDefault()).toInstant());
        return postRepository.findByMostInteractionsAfter(oldestRelevantActions, PageRequest.of(page, TREND_SIZE));
    }

    public Page<TrendInterface> getUserTrends(int page) {
        Date oldestRelevantActions = Date.from(LocalDateTime.now().minusMinutes(TREND_RELEVANT_TIMERANGE_MINUTES).atZone(ZoneId.systemDefault()).toInstant());
        return followedUserRepository.findByMostInteractionsAfter(oldestRelevantActions, PageRequest.of(page, TREND_SIZE));
    }
}

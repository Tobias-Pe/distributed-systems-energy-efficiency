package edu.hm.peslalz.thesis.statisticservice.service;

import edu.hm.peslalz.thesis.statisticservice.entity.*;
import edu.hm.peslalz.thesis.statisticservice.repository.ActionProtocolRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.CategoryRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.PostRepository;
import edu.hm.peslalz.thesis.statisticservice.repository.FollowedUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TrendService {
    public static final int TREND_SIZE = 10;
    public static final int TREND_RELEVANT_TIMERANGE_MINUTES = 2;

    CategoryRepository categoryRepository;
    FollowedUserRepository followedUserRepository;
    PostRepository postRepository;
    ActionProtocolRepository actionProtocolRepository;

    public TrendService(CategoryRepository categoryRepository, FollowedUserRepository followedUserRepository, PostRepository postRepository, ActionProtocolRepository actionProtocolRepository) {
        this.categoryRepository = categoryRepository;
        this.followedUserRepository = followedUserRepository;
        this.postRepository = postRepository;
        this.actionProtocolRepository = actionProtocolRepository;
    }

    public void registerAccountFollowed(Integer userId) {
        FollowedUser followedUser = followedUserRepository.findByUserId(userId).orElse(new FollowedUser(userId));
        followedUser.getInteractions().add(createActionProtocol("new follower"));
        followedUserRepository.save(followedUser);
    }

    private ActionProtocol createActionProtocol(String action) {
        ActionProtocol actionProtocol = new ActionProtocol(action);
        return this.actionProtocolRepository.save(actionProtocol);
    }

    public void registerPostAction(PostActionMessage postActionMessage) {
        Post post = postRepository.findByPostId(postActionMessage.getPostMessage().getId()).orElse(new Post(postActionMessage.getPostMessage().getId()));
        post.getInteractions().add(createActionProtocol(postActionMessage.getAction()));
        postRepository.save(post);
    }

    public void registerNewPost(PostMessage postMessage) {
        postMessage.getCategories().forEach(this::registerCategory);
    }

    private void registerCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName).orElse(new Category(categoryName));
        category.getInteractions().add(createActionProtocol("new post"));
        categoryRepository.save(category);
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

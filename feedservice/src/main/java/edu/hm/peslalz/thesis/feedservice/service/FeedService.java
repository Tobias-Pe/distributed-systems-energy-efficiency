package edu.hm.peslalz.thesis.feedservice.service;

import edu.hm.peslalz.thesis.feedservice.client.PostClient;
import edu.hm.peslalz.thesis.feedservice.client.TrendClient;
import edu.hm.peslalz.thesis.feedservice.entity.PagedTrendResponse;
import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import edu.hm.peslalz.thesis.feedservice.entity.Trend;
import edu.hm.peslalz.thesis.feedservice.repository.UserPreferenceRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedService {
    TrendClient trendClient;
    PostClient postClient;
    UserPreferenceRepository userPreferenceRepository;

    public FeedService(TrendClient trendClient, PostClient postClient, UserPreferenceRepository userPreferenceRepository) {
        this.trendClient = trendClient;
        this.postClient = postClient;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public Slice<PostDTO> getPersonalizedFeed(int userId, int page) {
        // Fetching trending categories, users, and posts in parallel
        CompletableFuture<List<String>> trendingCategoriesFuture = CompletableFuture.supplyAsync(() ->
                trendClient.getTrendingCategories(0, 5).getContent()
                        .stream()
                        .map(Trend::identifier)
                        .toList()
        );

        CompletableFuture<List<String>> trendingUsersFuture = CompletableFuture.supplyAsync(() ->
                trendClient.getTrendingUsers(0, 5).getContent()
                        .stream()
                        .map(Trend::identifier)
                        .toList()
        );

        CompletableFuture<PagedTrendResponse> trendingPostsFuture = CompletableFuture.supplyAsync(() ->
                trendClient.getTrendingPosts(0, 10)
        );

        // load preferences from repository
        List<String> userTopCategories = userPreferenceRepository.findByUserIdTopCategories(userId, Pageable.ofSize(5));
        List<String> userTopUsers = userPreferenceRepository.findByUserIdTopUsers(userId, Pageable.ofSize(5));

        List<String> trendingCategories = new ArrayList<>(trendingCategoriesFuture.join());
        trendingCategories.addAll(userTopCategories);

        List<String> trendingUsers = new ArrayList<>(trendingUsersFuture.join());
        trendingUsers.addAll(userTopUsers);

        PagedTrendResponse trendingPosts = trendingPostsFuture.join();

        return collectTrendingAndPreferredPosts(page, trendingCategories, trendingUsers, trendingPosts);
    }

    private Slice<PostDTO> collectTrendingAndPreferredPosts(int page, List<String> categories, List<String> contentCreators, PagedTrendResponse trendingPosts) {
        // Collect posts in parallel using parallelStream()
        List<PostDTO> posts = new ArrayList<>();

        // Fetch posts for categories in parallel
        List<PostDTO> categoryPosts = categories.parallelStream()
                .flatMap(id -> postClient.getPosts(id, null, page, 2).getContent().stream())
                .toList();

        // Fetch posts for content creators in parallel
        List<PostDTO> creatorPosts = contentCreators.parallelStream()
                .flatMap(id -> postClient.getPosts(null, Integer.valueOf(id), page, 2).getContent().stream())
                .toList();

        // Fetch trending posts in parallel
        List<PostDTO> trendingPostsList = trendingPosts.getContent().parallelStream()
                .map(trend -> postClient.getPost(Integer.parseInt(trend.identifier())))
                .toList();

        // Combine all posts
        posts.addAll(categoryPosts);
        posts.addAll(creatorPosts);
        posts.addAll(trendingPostsList);

        fillUpWithRecentPosts(page, posts);
        return new SliceImpl<>(posts);
    }

    private void fillUpWithRecentPosts(int page, List<PostDTO> posts) {
        if (posts.size() < 50) {
            posts.addAll(postClient.getPosts(null, null, page, 50 - posts.size()).getContent());
        }
    }
}

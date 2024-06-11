package edu.hm.peslalz.thesis.feedservice.service;

import edu.hm.peslalz.thesis.feedservice.client.PostClient;
import edu.hm.peslalz.thesis.feedservice.client.TrendClient;
import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import edu.hm.peslalz.thesis.feedservice.entity.Trend;
import edu.hm.peslalz.thesis.feedservice.repository.UserPreferenceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<String> trendingCategories = new ArrayList<>(trendClient.getTrendingCategories(0, 5).getContent()
                .stream()
                .map(Trend::identifier)
                .toList());
        trendingCategories.addAll(userPreferenceRepository.findByUserIdTopCategories(userId, Pageable.ofSize(5)));

        List<String> trendingUsers = new ArrayList<>(trendClient.getTrendingUsers(0, 5).getContent()
                .stream()
                .map(Trend::identifier)
                .toList());
        trendingUsers.addAll(userPreferenceRepository.findByUserIdTopUsers(userId, Pageable.ofSize(5)));

        Page<Trend> trendingPosts = trendClient.getTrendingPosts(0, 10);

        return collectTrendingAndPreferredPosts(page, trendingCategories, trendingUsers, trendingPosts);
    }

    private Slice<PostDTO> collectTrendingAndPreferredPosts(int page, List<String> categories, List<String> contentCreators, Page<Trend> trendingPosts) {
        List<PostDTO> posts = new ArrayList<>();
        categories.forEach(id -> posts.addAll(postClient.getPosts(id, null, page, 2).getContent()));
        contentCreators.forEach(id -> posts.addAll(postClient.getPosts(null, Integer.valueOf(id), page, 2).getContent()));
        trendingPosts.forEach(trendInterface -> posts.add(postClient.getPost(Integer.parseInt(trendInterface.identifier()))));
        fillUpWithRecentPosts(page, posts);
        return new SliceImpl<>(posts);
    }

    private void fillUpWithRecentPosts(int page, List<PostDTO> posts) {
        if (posts.size() < 50) {
            posts.addAll(postClient.getPosts(null, null, page, 50 - posts.size()).getContent());
        }
    }
}

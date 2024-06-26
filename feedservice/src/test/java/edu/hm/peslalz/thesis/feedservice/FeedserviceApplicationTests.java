package edu.hm.peslalz.thesis.feedservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.hm.peslalz.thesis.feedservice.client.PostClient;
import edu.hm.peslalz.thesis.feedservice.client.TrendClient;
import edu.hm.peslalz.thesis.feedservice.controller.FeedController;
import edu.hm.peslalz.thesis.feedservice.entity.PostDTO;
import edu.hm.peslalz.thesis.feedservice.entity.Trend;
import edu.hm.peslalz.thesis.feedservice.entity.UserPreference;
import edu.hm.peslalz.thesis.feedservice.repository.UserPreferenceRepository;
import edu.hm.peslalz.thesis.feedservice.service.FeedService;
import edu.hm.peslalz.thesis.feedservice.service.PreferencesReceiveService;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
class FeedserviceApplicationTests {
    @Autowired
    PreferencesReceiveService preferencesReceiveService;

    @Autowired
    FeedController feedController;

    @InjectMocks
    FeedService feedService;

    @MockBean
    PostClient postClient;

    @MockBean
    TrendClient trendClient;

    @Autowired
    UserPreferenceRepository userPreferenceRepository;

    @SneakyThrows
    @Test
    void receivePreferencesParallel() {
        IntStream.range(0, 20).parallel().forEach(i -> {
            try {
                preferencesReceiveService.receivePostAction(String.format("""
                        {
                          "userId": 12,
                          "action": "like",
                          "postMessage": {
                            "id": %d,
                            "userId": 123,
                            "text": "This is a sample post message.",
                            "likes": 50,
                            "comments": [
                              "Nice post!",
                              "Thanks for sharing."
                            ],
                            "categories": [
                              "Technology",
                              "Education"
                            ],
                            "hasImage": true
                          }
                        }
                        """, i));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        Optional<UserPreference> optionalUserPreference = userPreferenceRepository.findByUserId(12);
        Assertions.assertThat(optionalUserPreference).isPresent();
        UserPreference userPreference = optionalUserPreference.get();
        Assertions.assertThat(userPreference.getUserId()).isEqualTo(12);

        Assertions.assertThat(userPreference.getUserInteractions()).hasSize(1);
        Assertions.assertThat(userPreference.getUserInteractions()).containsEntry(123, 20);

        Assertions.assertThat(userPreference.getCategoryInteractions()).hasSize(2);
        Assertions.assertThat(userPreference.getCategoryInteractions()).containsEntry("Technology", 20);
        Assertions.assertThat(userPreference.getCategoryInteractions()).containsEntry("Education", 20);
    }

    @SneakyThrows
    @Test
    void receivePreferences() {
        preferencesReceiveService.receivePost("""
                {
                    "id": 12,
                    "userId": 123,
                    "text": "This is a sample post message.",
                    "likes": 50,
                    "comments": [
                      "Nice post!",
                      "Thanks for sharing."
                    ],
                    "categories": [
                      "Technology",
                      "Education"
                    ],
                    "hasImage": true
                }
                """);

        Optional<UserPreference> optionalUserPreference = userPreferenceRepository.findByUserId(123);
        Assertions.assertThat(optionalUserPreference).isPresent();
        UserPreference userPreference = optionalUserPreference.get();
        Assertions.assertThat(userPreference.getUserId()).isEqualTo(123);

        Assertions.assertThat(userPreference.getUserInteractions()).isEmpty();

        Assertions.assertThat(userPreference.getCategoryInteractions()).hasSize(2);
        Assertions.assertThat(userPreference.getCategoryInteractions()).containsEntry("Technology", 1);
        Assertions.assertThat(userPreference.getCategoryInteractions()).containsEntry("Education", 1);
    }

    @Test
    void testFeed() {
        Mockito.when(trendClient.getTrendingCategories(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(new Trend("Fishing", 12), new Trend("Outdoor", 6))));
        Mockito.when(trendClient.getTrendingPosts(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(new Trend("1", 12), new Trend("2", 6))));
        Mockito.when(trendClient.getTrendingUsers(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(new Trend("1", 12), new Trend("2", 6))));
        Mockito.when(postClient.getPosts(any(), any(),anyInt(),anyInt())).thenReturn(new PageImpl<>(List.of(new PostDTO(), new PostDTO())));
        Mockito.when(postClient.getPost(anyInt())).thenReturn(new PostDTO());
        Slice<PostDTO> personalizedFeed = feedController.getPersonalizedFeed(1, 0);
        Mockito.verify(postClient, Mockito.times(5)).getPosts(any(), any(), anyInt(), anyInt());
        Mockito.verify(postClient, Mockito.times(2)).getPost(anyInt());
        Assertions.assertThat(personalizedFeed).isNotNull();
        Assertions.assertThat(personalizedFeed.getContent()).isNotEmpty();
        Assertions.assertThat(personalizedFeed.getContent()).hasSize(12);
    }
}

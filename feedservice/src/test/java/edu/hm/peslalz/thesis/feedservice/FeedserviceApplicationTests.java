package edu.hm.peslalz.thesis.feedservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.hm.peslalz.thesis.feedservice.entity.UserPreference;
import edu.hm.peslalz.thesis.feedservice.repository.UserPreferenceRepository;
import edu.hm.peslalz.thesis.feedservice.service.PreferencesReceiveService;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
class FeedserviceApplicationTests {
    @Autowired
    PreferencesReceiveService preferencesReceiveService;

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

}

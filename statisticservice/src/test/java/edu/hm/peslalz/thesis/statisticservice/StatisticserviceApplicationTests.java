package edu.hm.peslalz.thesis.statisticservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.statisticservice.controller.TrendController;
import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import edu.hm.peslalz.thesis.statisticservice.service.StatisticsReceiveService;
import edu.hm.peslalz.thesis.statisticservice.service.TrendService;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

@SpringBootTest
class StatisticserviceApplicationTests {

    @Autowired
    StatisticsReceiveService statisticReceiveService;

    @Autowired
    TrendController trendController;

    @Test
    void testUserTrend() {
        statisticReceiveService.receiveUser("1");
        statisticReceiveService.receiveUser("2");
        statisticReceiveService.receiveUser("3");
        statisticReceiveService.receiveUser("1");
        statisticReceiveService.receiveUser("4");
        statisticReceiveService.receiveUser("2");
        statisticReceiveService.receiveUser("5");
        statisticReceiveService.receiveUser("1");

        Page<TrendInterface> trendingUsers = trendController.getTrendingUsers(0);
        Assertions.assertThat(trendingUsers.getNumberOfElements()).isEqualTo(5);
        List<TrendInterface> trendingUsersContent = trendingUsers.getContent();
        Assertions.assertThat(trendingUsersContent.getFirst().getIdentifier()).isEqualTo("1");
        Assertions.assertThat(trendingUsersContent.getFirst().getTrendPoints()).isEqualTo(3);
        Assertions.assertThat(trendingUsersContent.getLast().getIdentifier()).isEqualTo("5");
    }

    @SneakyThrows
    @Test
    void testCategoryTrend() {
        statisticReceiveService.receivePost(createPostMessageJsonWithCategories("fish", "meat", "a", "b", "c", "d"));
        statisticReceiveService.receivePost(createPostMessageJsonWithCategories("fish", "meat", "e", "f", "g", "h", "i", "j", "k", "l"));

        Page<TrendInterface> trendingCategories = trendController.getTrendingCategories(0);
        Assertions.assertThat(trendingCategories.getNumberOfElements()).isEqualTo(TrendService.TREND_SIZE);
        List<TrendInterface> trendingCategoriesContent = trendingCategories.getContent();
        Assertions.assertThat(trendingCategoriesContent.getFirst().getIdentifier()).hasSize(4);
        Assertions.assertThat(trendingCategoriesContent.getFirst().getTrendPoints()).isEqualTo(2);
        Assertions.assertThat(trendingCategoriesContent.getLast().getIdentifier()).hasSize(1);
    }

    @SneakyThrows
    @Test
    void testPostTrend() {
        statisticReceiveService.receivePostAction(createPostActionMessageJson("like", 1));
        statisticReceiveService.receivePostAction(createPostActionMessageJson("comment", 1));
        statisticReceiveService.receivePostAction(createPostActionMessageJson("comment", 1));
        statisticReceiveService.receivePostAction(createPostActionMessageJson("comment", 2));
        statisticReceiveService.receivePostAction(createPostActionMessageJson("comment", 2));
        statisticReceiveService.receivePostAction(createPostActionMessageJson("like", 3));

        Page<TrendInterface> trendingPosts = trendController.getTrendingPosts(0);
        Assertions.assertThat(trendingPosts.getNumberOfElements()).isEqualTo(3);
        List<TrendInterface> trendingPostsContent = trendingPosts.getContent();
        Assertions.assertThat(trendingPostsContent.getFirst().getIdentifier()).isEqualTo("1");
        Assertions.assertThat(trendingPostsContent.getFirst().getTrendPoints()).isEqualTo(3);
        Assertions.assertThat(trendingPostsContent.getLast().getTrendPoints()).isEqualTo(1);
    }

    String createPostMessageJsonWithCategories(String... categories) throws JsonProcessingException {
        return createPostMessageJsonWithCategories(1, categories);
    }

    String createPostMessageJsonWithCategories(Integer postId, String... categories) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String categoryJson;
        categoryJson = mapper.writeValueAsString(categories);

        return String.format("""
                {
                    "id": %d,
                    "userId": 10,
                    "text": "This is a sample text for the post!",
                    "likes": 0,
                    "comments": [],
                    "categories": %s,
                    "hasImage": false
                }
                """, postId, categoryJson);
    }

    String createPostActionMessageJson(String action, Integer postId) throws JsonProcessingException {
        String postMessageJson = createPostMessageJsonWithCategories(postId, "debugging", "testing");

        return String.format("""
                {
                    "userId": 789,
                    "action": "%s",
                    "postMessage": %s
                }
                """, action, postMessageJson);
    }

}

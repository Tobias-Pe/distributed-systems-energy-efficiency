package edu.hm.peslalz.thesis.notificationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.hm.peslalz.thesis.notificationservice.client.UserClient;
import edu.hm.peslalz.thesis.notificationservice.controller.NotificationController;
import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.entity.UserMessage;
import edu.hm.peslalz.thesis.notificationservice.service.NotificationService;
import edu.hm.peslalz.thesis.notificationservice.service.PostReceiverService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class NotificationserviceApplicationTests {

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private PostReceiverService postReceiverService;

    @SpyBean
    private NotificationService notificationService;

    @MockBean
    private UserClient userClient;

    @Test
    void receiveANewPost() throws JsonProcessingException {
        List<UserMessage> userMessages = new ArrayList<>();
        userMessages.add(new UserMessage(1,"contentLover",null,null,null));
        userMessages.add(new UserMessage(2,"notificationsLover",null,null,null));
        ResponseEntity<List<UserMessage>> responseEntity = ResponseEntity.ok().body(userMessages);
        when(userClient.getUserFollowers(anyInt())).thenReturn(responseEntity);
        postReceiverService.receive("""
                {
                  "id": 52,
                  "userId": 3,
                  "text": "New content new notification",
                  "likes": 0,
                  "comments": [],
                  "categories": ["contentCreatorStrikesAgain", "takeThis Inbox"],
                  "hasImage": true
                }
                """);
        verify(userClient, times(1)).getUserFollowers(anyInt());
        verify(notificationService, times(userMessages.size())).createNotification(any());
        Assertions.assertThat(new File(NotificationService.OUTPUT_FOLDERNAME).listFiles()).hasSize(2);
    }

    @Test
    void controllerTests() throws Exception {
        Notification notification = new Notification();
        notification.setId(3);
        notification.setNotifiedUsersId(3);
        notification.setPostId(3);
        notification.setPostingUsersId(4);
        notificationService.createNotification(notification);
        Assertions.assertThat(notificationController.getUsersNotifications(3).call()).contains(notification);
        Assertions.assertThat(notificationController.getNotificationCount(3, null).call()).isEqualTo(1);
        Assertions.assertThat(notificationController.getNotificationCount(3, false).call()).isEqualTo(1);
        notificationController.setReadStatus(3, true).call();
        Assertions.assertThat(notificationController.getNotificationCount(3, true).call()).isEqualTo(1);
    }

    @AfterAll
    static void afterAll() throws IOException {
        File inbox = new File(NotificationService.OUTPUT_FOLDERNAME);
        for (File file : inbox.listFiles()) {
            FileUtils.deleteDirectory(file);
        }
        FileUtils.deleteDirectory(inbox);
        if(inbox.getParentFile().listFiles().length == 0) {
            FileUtils.deleteDirectory(inbox.getParentFile());
        }

    }
}

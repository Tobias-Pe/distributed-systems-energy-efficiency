package edu.hm.peslalz.thesis.notificationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.notificationservice.controller.NotificationController;
import edu.hm.peslalz.thesis.notificationservice.entity.FollowerDto;
import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.service.NotificationService;
import edu.hm.peslalz.thesis.notificationservice.service.PostReceiverService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
    private RabbitTemplate template;

    @Test
    void receiveANewPost() throws JsonProcessingException {
        Set<FollowerDto> followerDtos = new HashSet<>();
        followerDtos.add(new FollowerDto(1, "contentLover"));
        followerDtos.add(new FollowerDto(2, "notificationsLover"));
        ObjectMapper mapper = new ObjectMapper();
        String message;
        try {
            message = mapper.writeValueAsString(followerDtos);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request to json", e);
        }
        when(template.convertSendAndReceive(any(String.class), any(String.class), any(Integer.class))).thenReturn(message);
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
        verify(template, times(1)).convertSendAndReceive(any(String.class), any(String.class), any(Integer.class));
        verify(notificationService, times(followerDtos.size())).createNotification(any());
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
        if (inbox.getParentFile().listFiles().length == 0) {
            FileUtils.deleteDirectory(inbox.getParentFile());
        }

    }
}

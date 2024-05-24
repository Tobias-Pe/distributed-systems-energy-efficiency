package edu.hm.peslalz.thesis.notificationservice.service;

import edu.hm.peslalz.thesis.notificationservice.client.UserClient;
import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.entity.PostMessage;
import edu.hm.peslalz.thesis.notificationservice.entity.UserMessage;
import edu.hm.peslalz.thesis.notificationservice.repository.NotificationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class NotificationService {
    NotificationRepository notificationRepository;
    UserClient userClient;

    @Value("classpath:email-template.html")
    Resource templateFile;
    public static final String OUTPUT_FOLDERNAME = "notificationservice/inbox/";

    public NotificationService(NotificationRepository notificationRepository, UserClient userClient) {
        this.notificationRepository = notificationRepository;
        this.userClient = userClient;
    }

    public Set<Notification> getUsersNotifications(Integer userId) {
        return notificationRepository.findAllByNotifiedUsersId(userId);
    }

    public Long countNotificationOfUser(Integer userId, Boolean wasRead) {
        if (wasRead != null) {
            return notificationRepository.countByAndNotifiedUsersIdIsAndWasReadIs(userId, wasRead);
        }
        return notificationRepository.countByAndNotifiedUsersIdIs(userId);
    }

    public Notification setReadStatus(Integer notificationId, boolean wasRead) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (!notification.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Notification with id %s not found", notificationId));
        }
        notification.get().setWasRead(wasRead);
        return notificationRepository.save(notification.get());
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void notifySubscriber(PostMessage postMessage) {
        ResponseEntity<List<UserMessage>> responseEntity = userClient.getUserFollowers(postMessage.getUserId());
        List<UserMessage> followers = responseEntity.getBody();
        for (UserMessage follower : followers) {
            simulateEmailSending(follower, String.valueOf(postMessage.getId()));
            Notification notification = new Notification(postMessage);
            notification.setNotifiedUsersId(follower.getId());
            createNotification(notification);
        }
    }

    public void simulateEmailSending(UserMessage user, String postId) {
        try {
            // Read template content
            String templateContent = templateFile.getContentAsString(Charset.defaultCharset());

            // Replace placeholders with simulated data
            String personalizedContent = templateContent
                    .replace("{{username}}", user.getUsername())
                    .replace("{{userId}}", user.getId().toString())
                    .replace("{{postId}}", postId);

            // Simulate sending the email by writing to a file (output path)
            Path directoryPath = Paths.get(String.format("%s/%s", OUTPUT_FOLDERNAME, user.getUsername()));
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            String outputFile = "sent-email-" + postId + "-" + UUID.randomUUID() + ".html";
            Files.writeString(Path.of(String.format("%s/%s", directoryPath, outputFile)),
                    personalizedContent, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}

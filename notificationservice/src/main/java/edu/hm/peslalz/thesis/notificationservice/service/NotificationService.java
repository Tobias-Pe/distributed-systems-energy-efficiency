package edu.hm.peslalz.thesis.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hm.peslalz.thesis.notificationservice.entity.FollowerDto;
import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.entity.PostMessage;
import edu.hm.peslalz.thesis.notificationservice.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class NotificationService {
    private final DirectExchange userserviceExchange;
    private final RabbitTemplate template;

    NotificationRepository notificationRepository;

    private final Counter notificationsCounter;

    @Value("classpath:email-template.html")
    Resource templateFile;

    public static final String OUTPUT_FOLDERNAME = "notificationservice/inbox/";

    public NotificationService(NotificationRepository notificationRepository, MeterRegistry registry, DirectExchange exchange, RabbitTemplate template) {
        this.notificationRepository = notificationRepository;
        this.notificationsCounter = Counter.builder("notificationservice_created_notifications_counter")
                .description("Count of created notifications")
                .register(registry);
        this.template = template;
        this.userserviceExchange = exchange;
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
        if (notification.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Notification with id %s not found", notificationId));
        }
        notification.get().setWasRead(wasRead);
        return notificationRepository.save(notification.get());
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // https://medium.com/@ayushgupta60/springboot-retry-transaction-marked-as-rollback-74ab21733469
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 100, maxDelay = 1000, multiplier = 2)
    )
    public void notifySubscriber(PostMessage postMessage) {
        String followersStr = (String) this.template.convertSendAndReceive(this.userserviceExchange.getName(), "rpc", postMessage.getUserId());
        ObjectMapper mapper = new ObjectMapper();
        Set<FollowerDto> followers = null;
        try {
            followers = mapper.readValue(followersStr, new TypeReference<Set<FollowerDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        followers.parallelStream().forEach(follower -> {
            simulateEmailSending(follower, String.valueOf(postMessage.getId()));
            Notification notification = new Notification(postMessage);
            notification.setNotifiedUsersId(follower.getId());
            createNotification(notification);
            notificationsCounter.increment();
        });
    }

    public void simulateEmailSending(FollowerDto user, String postId) {
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

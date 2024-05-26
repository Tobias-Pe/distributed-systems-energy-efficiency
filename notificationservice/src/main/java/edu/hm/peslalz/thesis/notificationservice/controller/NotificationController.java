package edu.hm.peslalz.thesis.notificationservice.controller;

import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("notifications")
@Log4j2
public class NotificationController {
    NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Set the read status of the notification")
    @PostMapping("/{id}")
    public Notification setReadStatus(@PathVariable(name = "id") int notificationId, @RequestParam("isRead") boolean isRead) {
        log.info("Setting read status of notification {} to {}", notificationId, isRead);
        return notificationService.setReadStatus(notificationId, isRead);
    }

    @Operation(summary = "Count how many notifications a user has with query option for the read status")
    @PostMapping("/count")
    public Long getNotificationCount(@RequestParam(name = "userId") int userId,
                                     @RequestParam(name = "wasRead", required = false) Boolean wasRead) {
        log.info("Count notifications of user {} with query option {}", userId, wasRead);
        return notificationService.countNotificationOfUser(userId, wasRead);
    }

    @Operation(summary = "Find all notifications an user has")
    @GetMapping
    public Set<Notification> getUsersNotifications(@RequestParam(name = "userId") int userId) {
        log.info("Find all notifications of user {}", userId);
        return notificationService.getUsersNotifications(userId);
    }

}

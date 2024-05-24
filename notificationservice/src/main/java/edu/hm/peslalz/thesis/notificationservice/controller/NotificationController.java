package edu.hm.peslalz.thesis.notificationservice.controller;

import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import edu.hm.peslalz.thesis.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("notifications")
public class NotificationController {
    NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Set the read status of the notification")
    @PostMapping("/{id}")
    public Notification setReadStatus(@PathVariable(name = "id") int notificationId, @RequestParam("isRead") boolean isRead) {
        return notificationService.setReadStatus(notificationId, isRead);
    }

    @Operation(summary = "Count how many notifications a user has with query option for the read status")
    @PostMapping("/count")
    public Long getNotificationCount(@RequestParam(name = "userId") int userId,
                                     @RequestParam(name = "wasRead", required = false) Boolean wasRead) {
        return notificationService.countNotificationOfUser(userId, wasRead);
    }

    @Operation(summary = "Find all notifications an user has")
    @GetMapping
    public Set<Notification> getUsersNotifications(@RequestParam(name = "userId") int userId) {
        return notificationService.getUsersNotifications(userId);
    }

}

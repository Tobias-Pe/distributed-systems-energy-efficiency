package edu.hm.peslalz.thesis.notificationservice.controller;

import edu.hm.peslalz.thesis.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notification")
public class NotificationController {
    NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


}

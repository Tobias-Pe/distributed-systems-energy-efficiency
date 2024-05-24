package edu.hm.peslalz.thesis.notificationservice.repository;

import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}

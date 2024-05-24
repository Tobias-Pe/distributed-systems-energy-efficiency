package edu.hm.peslalz.thesis.notificationservice.repository;

import edu.hm.peslalz.thesis.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Set<Notification> findAllByNotifiedUsersId(Integer userId);

    long countByAndNotifiedUsersIdIs(Integer notifiedUserId);

    long countByAndNotifiedUsersIdIsAndWasReadIs(Integer notifiedUserId, boolean wasRead);
}

package edu.hm.peslalz.thesis.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer id;

    @Column(nullable = false)
    private Integer postId;

    @Column(nullable = false)
    private Integer postingUsersId;

    @Column(nullable = false)
    private Integer notifiedUsersId;

    @Column(nullable = false)
    private boolean wasRead = false;

    public Notification(PostMessage postMessage) {
        this.postId = postMessage.getId();
        this.postingUsersId = postMessage.getUserId();
    }
}

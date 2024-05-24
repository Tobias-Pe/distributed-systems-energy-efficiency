package edu.hm.peslalz.thesis.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Notification {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return wasRead == that.wasRead && Objects.equals(id, that.id) && Objects.equals(postId, that.postId) && Objects.equals(postingUsersId, that.postingUsersId) && Objects.equals(notifiedUsersId, that.notifiedUsersId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, postingUsersId, notifiedUsersId, wasRead);
    }

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

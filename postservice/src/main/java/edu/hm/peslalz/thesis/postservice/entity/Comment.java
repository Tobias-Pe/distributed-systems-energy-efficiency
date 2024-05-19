package edu.hm.peslalz.thesis.postservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "text", nullable = false, length = 500)
    private String text;

    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    public Comment(CommentRequest commentRequest) {
        this.text = commentRequest.getText();
        this.userId = commentRequest.getUserId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;
        return Objects.equals(getId(), comment.getId()) && getUserId().equals(comment.getUserId()) && getText().equals(comment.getText()) && getLikes().equals(comment.getLikes());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + getUserId().hashCode();
        result = 31 * result + getText().hashCode();
        result = 31 * result + getLikes().hashCode();
        return result;
    }

    @Version
    Integer version;
}

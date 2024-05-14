package edu.hm.peslalz.thesis.postservice.entity;

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
}

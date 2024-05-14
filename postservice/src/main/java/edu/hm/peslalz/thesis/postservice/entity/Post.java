package edu.hm.peslalz.thesis.postservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Post {
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

    @OneToMany
    private Set<Comment> comments= new HashSet<>();

    @ManyToMany
    private Set<Category> categories = new HashSet<>();

    public Post(PostRequest postRequest) {
        this.userId = postRequest.getUserId();
        this.text = postRequest.getText();
        this.categories = postRequest.getCategories();
    }
}
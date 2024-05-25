package edu.hm.peslalz.thesis.postservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("posts")
    private Set<Category> categories = new HashSet<>();

    public Post(PostRequest postRequest, MultipartFile image) {
        this.userId = postRequest.getUserId();
        this.text = postRequest.getText();
        this.categories = postRequest.getCategories().stream().map(Category::new).collect(Collectors.toSet());

        if (image == null) {
            return;
        }
        this.imageData = new ImageData(image);
    }

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private ImageData imageData;

    @Version
    @JsonIgnore
    Integer version;
}

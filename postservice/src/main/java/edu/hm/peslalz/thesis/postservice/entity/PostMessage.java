package edu.hm.peslalz.thesis.postservice.entity;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostMessage implements Serializable {
    private Integer id;

    private Integer userId;

    private String text;

    private Integer likes = 0;

    private Set<String> comments = new HashSet<>();

    private Set<String> categories = new HashSet<>();

    private boolean hasImage = false;

    public PostMessage(Post post) {
        this.id = post.getId();
        this.userId = post.getUserId();
        this.text = post.getText();
        this.likes = post.getLikes();
        this.comments = post.getComments().stream().map(Comment::getText).collect(Collectors.toSet());
        this.categories = post.getCategories().stream().map(Category::getName).collect(Collectors.toSet());
        this.hasImage = post.getImageData() != null;
    }
}

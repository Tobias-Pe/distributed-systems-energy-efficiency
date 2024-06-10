package edu.hm.peslalz.thesis.feedservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class PostMessage implements Serializable {
    private Integer id;

    private Integer userId;

    private String text;

    private Integer likes;

    private Set<String> comments;

    private Set<String> categories;

    private boolean hasImage;
}

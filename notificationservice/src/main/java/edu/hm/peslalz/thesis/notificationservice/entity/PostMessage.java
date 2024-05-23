package edu.hm.peslalz.thesis.notificationservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PostMessage implements Serializable {
    private Integer id;

    private Integer userId;

    private String text;

    private Integer likes = 0;

    private Set<String> comments = new HashSet<>();

    private Set<String> categories = new HashSet<>();

    private boolean hasImage = false;
}

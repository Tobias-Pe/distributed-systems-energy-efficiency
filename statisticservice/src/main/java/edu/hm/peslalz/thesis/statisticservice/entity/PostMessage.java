package edu.hm.peslalz.thesis.statisticservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class PostMessage {
    private Integer id;

    private Integer userId;

    private String text;

    private Integer likes = 0;

    private Set<String> comments = new HashSet<>();

    private Set<String> categories = new HashSet<>();

    private boolean hasImage = false;
}

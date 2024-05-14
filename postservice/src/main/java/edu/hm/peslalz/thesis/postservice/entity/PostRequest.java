package edu.hm.peslalz.thesis.postservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class PostRequest {
    private Integer userId;

    private String text;

    private Set<Category> categories = new HashSet<>();
}

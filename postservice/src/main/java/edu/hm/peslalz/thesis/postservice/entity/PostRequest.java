package edu.hm.peslalz.thesis.postservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private Integer userId;

    private String text;

    private Set<String> categories = new HashSet<>();
}

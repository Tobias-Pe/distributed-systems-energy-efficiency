package edu.hm.peslalz.thesis.postservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CommentRequest {
    private Integer userId;

    private String text;
}

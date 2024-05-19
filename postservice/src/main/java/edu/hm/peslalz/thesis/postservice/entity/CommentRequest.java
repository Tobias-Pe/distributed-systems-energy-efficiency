package edu.hm.peslalz.thesis.postservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private Integer userId;

    private String text;
}

package edu.hm.peslalz.thesis.postservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostActionMessage implements Serializable {
    private Integer postId;

    private Integer userId;

    private String action;
}

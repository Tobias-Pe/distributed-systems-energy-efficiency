package edu.hm.peslalz.thesis.statisticservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PostActionMessage {
    private Integer userId;

    private String action;

    private PostMessage postMessage;
}

package edu.hm.peslalz.thesis.notificationservice.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserMessage {
    private Integer id;
    private String username;
    private List<UserMessage> following;
    private Date created;
    private Date updated;
}

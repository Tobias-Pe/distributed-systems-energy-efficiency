package edu.hm.peslalz.thesis.notificationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {
    private Integer id;
    private String username;
    private List<UserMessage> following;
    private Date created;
    private Date updated;
}

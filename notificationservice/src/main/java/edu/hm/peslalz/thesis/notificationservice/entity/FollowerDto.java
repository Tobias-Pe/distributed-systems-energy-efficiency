package edu.hm.peslalz.thesis.notificationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FollowerDto implements Serializable {
    Integer id;
    String username;
}

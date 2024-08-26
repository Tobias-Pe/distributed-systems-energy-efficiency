package edu.hm.peslalz.thesis.userservice.entity;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link edu.hm.peslalz.thesis.userservice.entity.UserAccount}
 */
@Value
public class FollowerDto implements Serializable {
    Integer id;
    String username;
}
package edu.hm.peslalz.thesis.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserConstraintConflictException extends  RuntimeException {
    public UserConstraintConflictException(String username) {
        super("User conflicting logical constraints: " + username);
    }
}

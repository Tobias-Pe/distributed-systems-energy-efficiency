package edu.hm.peslalz.thesis.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends  RuntimeException {
    public UserNotFoundException(String username) {
        super("Could not find user " + username);
    }
}

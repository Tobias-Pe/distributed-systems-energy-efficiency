package edu.hm.peslalz.thesis.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameTakenException extends  RuntimeException {
    public UsernameTakenException(String username) {
        super("Username is already taken: " + username);
    }
}

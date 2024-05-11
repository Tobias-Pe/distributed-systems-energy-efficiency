package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserAccountController {
    UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping()
    public UserAccount getUserAccount(@RequestParam(required = false) String username, @RequestParam(required = false) Integer id) {
        if (id != null) {
            return userAccountService.getUserById(id);
        }
        return userAccountService.getUserByUsername(username);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        return userAccountService.createUser(userAccountRequest);
    }
}

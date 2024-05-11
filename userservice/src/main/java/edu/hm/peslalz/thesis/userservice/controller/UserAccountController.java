package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserAccountController {
    UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/username/{username}")
    public UserAccount getUserAccount(@PathVariable String username) {
        return userAccountService.getUserByUsername(username);
    }

    @GetMapping("/id/{id}")
    public UserAccount getUserAccount(@PathVariable Integer id) {
        return userAccountService.getUserById(id);
    }

    @PostMapping()
    public UserAccount createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        return userAccountService.createUser(userAccountRequest);
    }
}

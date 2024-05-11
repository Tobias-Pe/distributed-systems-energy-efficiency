package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("users")
public class UserAccountController {
    UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        return userAccountService.createUser(userAccountRequest);
    }

    @PostMapping("/search")
    public Set<UserAccount> searchUser(@RequestParam String query) {
        return userAccountService.search(query);
    }

    @GetMapping("/{id}")
    public UserAccount getUserAccount(@PathVariable int id) {
        return userAccountService.getUserById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount updateUser(@RequestBody UserAccountRequest userAccountRequest, @PathVariable Integer id) {
        return userAccountService.updateUser(userAccountRequest, id);
    }

    @PostMapping("/{id}/follow")
    public UserAccount followUser(@RequestParam String toBeFollowedUsername, @PathVariable Integer id) {
        return userAccountService.follow(id, toBeFollowedUsername);
    }

    @GetMapping("/{id}/followers")
    public Set<UserAccount> getFollowers(@PathVariable Integer id) {
        return userAccountService.getFollowers(id);
    }
}

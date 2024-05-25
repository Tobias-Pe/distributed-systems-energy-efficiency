package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("users")
@Log4j2
public class UserAccountController {
    UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(description = "Create a user")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        log.info("Create user account: {}", userAccountRequest.getUsername());
        return userAccountService.createUser(userAccountRequest);
    }

    @Operation(description = "Search a user (% for wildcard)")
    @PostMapping("/search")
    public Page<UserAccount> searchUser(@RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        log.info("Search user accounts like: {}", query);
        return userAccountService.search(query, page);
    }

    @Operation(description = "Get information to a user")
    @GetMapping("/{id}")
    public UserAccount getUserAccount(@PathVariable int id) {
        log.info("Get user account with id: {}", id);
        return userAccountService.getUserById(id);
    }

    @Operation(description = "Update a user")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount updateUser(@RequestBody UserAccountRequest userAccountRequest, @PathVariable Integer id) {
        log.info("Update user account: {}", id);
        return userAccountService.updateUser(userAccountRequest, id);
    }

    @Operation(description = "Follow a user")
    @PutMapping("/{id}/follow")
    public UserAccount followUser(@RequestParam String toBeFollowedUsername, @PathVariable Integer id) {
        log.info("User {} follow request for: {}", id, toBeFollowedUsername);
        return userAccountService.follow(id, toBeFollowedUsername);
    }

    @Operation(description = "Get followers of a user")
    @GetMapping("/{id}/followers")
    public Set<UserAccount> getFollowers(@PathVariable Integer id) {
        log.info("Get followers of user: {}", id);
        return userAccountService.getFollowers(id);
    }
}

package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.FollowerDto;
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
import java.util.concurrent.Callable;

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
    public Callable<UserAccount> createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        return () -> {
            log.info("Create user account: {}", userAccountRequest.getUsername());
            return userAccountService.createUser(userAccountRequest);
        };
    }

    @Operation(description = "Search a user (% for wildcard)")
    @PostMapping("/search")
    public Callable<Page<UserAccount>> searchUser(@RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        return () -> {
            log.info("Search user accounts like: {}", query);
            return userAccountService.search(query, page);
        };
    }

    @Operation(description = "Get information to a user")
    @GetMapping("/{id}")
    public Callable<UserAccount> getUserAccount(@PathVariable int id) {
        return () -> {
            log.info("Get user account with id: {}", id);
            return userAccountService.getUserById(id);
        };
    }

    @Operation(description = "Update a user")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Callable<UserAccount> updateUser(@RequestBody UserAccountRequest userAccountRequest, @PathVariable Integer id) {
        return () -> {
            log.info("Update user account: {}", id);
            return userAccountService.updateUser(userAccountRequest, id);
        };
    }

    @Operation(description = "Follow a user")
    @PutMapping("/{id}/follow")
    public Callable<UserAccount> followUser(@RequestParam String toBeFollowedUsername, @PathVariable Integer id) {
        return () -> {
            log.info("User {} follow request for: {}", id, toBeFollowedUsername);
            return userAccountService.follow(id, toBeFollowedUsername);
        };
    }

    @Operation(description = "Get followers of a user")
    @GetMapping("/{id}/followers")
    public Callable<Set<FollowerDto>> getFollowers(@PathVariable Integer id) {
        return () -> {
            log.info("Get followers of user: {}", id);
            return userAccountService.getFollowers(id);
        };
    }
}

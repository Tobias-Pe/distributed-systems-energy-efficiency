package edu.hm.peslalz.thesis.userservice.controller;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("users")
public class UserAccountController {
    UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(description = "Create a user")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount createUserAccount(@RequestBody UserAccountRequest userAccountRequest) {
        return userAccountService.createUser(userAccountRequest);
    }

    @Operation(description = "Search a user (% for wildcard)")
    @PostMapping("/search")
    public Page<UserAccount> searchUser(@RequestParam String query, @RequestParam(defaultValue = "0") int page) {
        return userAccountService.search(query, page);
    }

    @Operation(description = "Get information to a user")
    @GetMapping("/{id}")
    public UserAccount getUserAccount(@PathVariable int id) {
        return userAccountService.getUserById(id);
    }

    @Operation(description = "Update a user")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccount updateUser(@RequestBody UserAccountRequest userAccountRequest, @PathVariable Integer id) {
        return userAccountService.updateUser(userAccountRequest, id);
    }

    @Operation(description = "Follow a user")
    @PutMapping("/{id}/follow")
    public UserAccount followUser(@RequestParam String toBeFollowedUsername, @PathVariable Integer id) {
        return userAccountService.follow(id, toBeFollowedUsername);
    }

    @Operation(description = "Get followers of a user")
    @GetMapping("/{id}/followers")
    public Set<UserAccount> getFollowers(@PathVariable Integer id) {
        return userAccountService.getFollowers(id);
    }
}

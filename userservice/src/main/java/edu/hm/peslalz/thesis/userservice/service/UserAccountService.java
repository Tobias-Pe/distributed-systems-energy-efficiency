package edu.hm.peslalz.thesis.userservice.service;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.exceptions.UserNotFoundException;
import edu.hm.peslalz.thesis.userservice.exceptions.UsernameTakenException;
import edu.hm.peslalz.thesis.userservice.repository.UserAccountRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserAccountService {
    UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount createUser(UserAccountRequest userAccountRequest) {
        UserAccount userAccount = new UserAccount(userAccountRequest);
        try {
            userAccount = userAccountRepository.save(userAccount);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new UsernameTakenException(userAccountRequest.getUsername());
            } else {
                throw ex;
            }
        }
        return userAccount;
    }

    public UserAccount getUserByUsername(String username) {
        return userAccountRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserAccount getUserById(Integer id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    public Page<UserAccount> search(String query, int page) {
        return userAccountRepository.findUserAccountByUsernameLike(query, PageRequest.of(page, 50));
    }

    public UserAccount updateUser(UserAccountRequest userAccountRequest, Integer id) {
        UserAccount userAccount = getUserById(id);
        userAccount.setUsername(userAccountRequest.getUsername());
        userAccountRepository.save(userAccount);
        return userAccount;
    }

    public UserAccount follow(Integer id, String toBeFollowedUsername) {
        UserAccount userAccount = getUserById(id);
        UserAccount toBeFollowedUserAccount = getUserByUsername(toBeFollowedUsername);
        userAccount.getFollowing().add(toBeFollowedUserAccount);
        userAccountRepository.save(userAccount);
        return userAccount;
    }

    public Set<UserAccount> getFollowers(Integer id) {
        UserAccount userAccount = getUserById(id);
        return userAccountRepository.findUserAccountsByFollowingContaining(userAccount);
    }
}

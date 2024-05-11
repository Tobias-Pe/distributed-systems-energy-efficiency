package edu.hm.peslalz.thesis.userservice.service;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.exceptions.UserNotFoundException;
import edu.hm.peslalz.thesis.userservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return userAccountRepository.save(userAccount);
    }

    public UserAccount getUserByUsername(String username) {
        return userAccountRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserAccount getUserById(Integer id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    public Set<UserAccount> search(String query) {
        return userAccountRepository.findUserAccountByUsernameLike(query);
    }
}

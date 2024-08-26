package edu.hm.peslalz.thesis.userservice.service;

import edu.hm.peslalz.thesis.userservice.entity.FollowerDto;
import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.exceptions.UserNotFoundException;
import edu.hm.peslalz.thesis.userservice.exceptions.UserConstraintConflictException;
import edu.hm.peslalz.thesis.userservice.repository.UserAccountRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserAccountService {
    UserAccountRepository userAccountRepository;

    private final RabbitTemplate template;

    private final FanoutExchange fanoutExchange;

    @Autowired
    public UserAccountService(RabbitTemplate template, FanoutExchange fanoutExchange, UserAccountRepository userAccountRepository) {
        this.fanoutExchange = fanoutExchange;
        this.userAccountRepository = userAccountRepository;
        this.template = template;
        // enable tracing for rabbitmq template
        this.template.setObservationEnabled(true);
    }

    public UserAccount createUser(UserAccountRequest userAccountRequest) {
        UserAccount userAccount = new UserAccount(userAccountRequest);
        userAccount = saveUser(userAccount);
        return userAccount;
    }

    UserAccount saveUser(UserAccount userAccount) throws UserConstraintConflictException {
        try {
            userAccount = userAccountRepository.save(userAccount);
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new UserConstraintConflictException(userAccount.getUsername());
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

    public boolean existUserWithId(Integer id) {
        return userAccountRepository.existsById(id);
    }

    public Page<UserAccount> search(String query, int page) {
        return userAccountRepository.findUserAccountByUsernameLike(query, PageRequest.of(page, 50));
    }

    public UserAccount updateUser(UserAccountRequest userAccountRequest, Integer id) {
        UserAccount userAccount = getUserById(id);
        userAccount.setUsername(userAccountRequest.getUsername());
        this.saveUser(userAccount);
        return userAccount;
    }

    public UserAccount follow(Integer id, String toBeFollowedUsername) {
        UserAccount userAccount = getUserById(id);
        UserAccount toBeFollowedUserAccount = getUserByUsername(toBeFollowedUsername);
        if (userAccount.getFollowing().contains(toBeFollowedUserAccount)) {
            return userAccount;
        }
        userAccount.getFollowing().add(toBeFollowedUserAccount);
        this.saveUser(userAccount);
        template.convertAndSend(fanoutExchange.getName(), "", toBeFollowedUserAccount.getId());
        return userAccount;
    }

    public Set<FollowerDto> getFollowers(Integer id) {
        UserAccount userAccount = getUserById(id);
        return userAccountRepository.findUserAccountsByFollowingContaining(userAccount);
    }
}

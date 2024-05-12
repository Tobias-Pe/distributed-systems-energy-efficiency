package edu.hm.peslalz.thesis.userservice.service;

import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import edu.hm.peslalz.thesis.userservice.entity.UserAccountRequest;
import edu.hm.peslalz.thesis.userservice.exceptions.UserConstraintConflictException;
import edu.hm.peslalz.thesis.userservice.exceptions.UserNotFoundException;
import edu.hm.peslalz.thesis.userservice.repository.UserAccountRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    UserAccountRepository userAccountRepository;

    @InjectMocks
    UserAccountService userAccountService;

    @Test
    void createUser() {
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserAccountRequest userAccountRequest = new UserAccountRequest("Testias");
        UserAccount user = userAccountService.createUser(userAccountRequest);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(userAccountRequest.getUsername());
    }

    @Test
    void getUserByUsername() {
        assertThrows(UserNotFoundException.class, () -> userAccountService.getUserByUsername("Testias"));
        when(userAccountRepository.findByUsername(any())).thenReturn(Optional.of(new UserAccount()));
        assertDoesNotThrow(() -> userAccountService.getUserByUsername("Testias"));
        verify(userAccountRepository, times(2)).findByUsername(any());
    }

    @Test
    void getUserById() {
        assertThrows(UserNotFoundException.class, () -> userAccountService.getUserById(1));
        when(userAccountRepository.findById(any())).thenReturn(Optional.of(new UserAccount()));
        assertDoesNotThrow(() -> userAccountService.getUserById(1));
        verify(userAccountRepository, times(2)).findById(any());
    }

    @Test
    void search() {
        assertThat(userAccountService.search("Testias", 0)).isNull();
        verify(userAccountRepository, times(1)).findUserAccountByUsernameLike("Testias", PageRequest.of(0,50));
    }

    @Test
    void updateUser() {
        when(userAccountRepository.findById(any())).thenReturn(Optional.of(new UserAccount()));
        assertDoesNotThrow(() -> userAccountService.updateUser(new UserAccountRequest("Testias"),1));
        verify(userAccountRepository, times(1)).findById(1);
        verify(userAccountRepository, times(1)).save(any());
    }

    @Test
    void follow() {
        when(userAccountRepository.findById(any())).thenReturn(Optional.of(new UserAccount()));
        when(userAccountRepository.findByUsername(any())).thenReturn(Optional.of(new UserAccount()));
        assertThat(userAccountService.follow(1, "Testias").getFollowing()).hasSize(1);
    }

    @Test
    void getFollowers() {
        when(userAccountRepository.findById(any())).thenReturn(Optional.of(new UserAccount()));
        userAccountService.getFollowers(1);
        verify(userAccountRepository, times(1)).findUserAccountsByFollowingContaining(any());
    }

    @Test
    void saveUser() {
        when(userAccountRepository.save(any(UserAccount.class))).thenThrow(new DataIntegrityViolationException("bla",new ConstraintViolationException("bla",new SQLException(),"nono")));
        assertThrows(UserConstraintConflictException.class, () -> userAccountService.saveUser(new UserAccount()));
    }
}
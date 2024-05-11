package edu.hm.peslalz.thesis.userservice.repository;


import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);

    Set<UserAccount> findUserAccountByUsernameLike(String query);

    Set<UserAccount> findUserAccountsByFollowingContaining(UserAccount userAccount);
}

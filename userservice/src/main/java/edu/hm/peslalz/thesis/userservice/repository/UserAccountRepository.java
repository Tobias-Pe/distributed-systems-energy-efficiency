package edu.hm.peslalz.thesis.userservice.repository;


import edu.hm.peslalz.thesis.userservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);
}

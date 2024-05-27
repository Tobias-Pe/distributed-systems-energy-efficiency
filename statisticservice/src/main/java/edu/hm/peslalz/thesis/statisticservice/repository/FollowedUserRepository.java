package edu.hm.peslalz.thesis.statisticservice.repository;

import edu.hm.peslalz.thesis.statisticservice.entity.FollowedUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowedUserRepository extends JpaRepository<FollowedUser, Integer> {
    @EntityGraph(attributePaths = "interactions")
    Optional<FollowedUser> findByUserId(int userId);
}

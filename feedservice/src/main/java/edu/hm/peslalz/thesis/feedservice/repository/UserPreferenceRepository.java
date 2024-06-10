package edu.hm.peslalz.thesis.feedservice.repository;

import edu.hm.peslalz.thesis.feedservice.entity.UserPreference;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    @Lock(LockModeType.OPTIMISTIC)
    @EntityGraph(attributePaths = {"categoryInteractions", "userInteractions"})
    @Transactional
    Optional<UserPreference> findByUserId(Integer userId);
}

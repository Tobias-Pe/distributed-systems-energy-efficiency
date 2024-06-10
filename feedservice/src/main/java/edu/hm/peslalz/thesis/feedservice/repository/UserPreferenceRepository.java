package edu.hm.peslalz.thesis.feedservice.repository;

import edu.hm.peslalz.thesis.feedservice.entity.UserPreference;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    @Lock(LockModeType.OPTIMISTIC)
    @EntityGraph(attributePaths = {"categoryInteractions", "userInteractions"})
    @Transactional
    Optional<UserPreference> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"categoryInteractions"})
    @Query("""
            select key(u.categoryInteractions)
            from UserPreference u
            where u.userId = :userId
            order by value(u.categoryInteractions) DESC
            """)
    List<String> findByUserIdTopCategories(Integer userId, Pageable pageable);

    @EntityGraph(attributePaths = {"userInteractions"})
    @Query("""
            select key(u.userInteractions)
            from UserPreference u
            where u.userId = :userId
            order by value(u.userInteractions) DESC
            """)
    List<String> findByUserIdTopUsers(Integer userId, Pageable pageable);
}

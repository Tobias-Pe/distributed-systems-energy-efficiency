package edu.hm.peslalz.thesis.statisticservice.repository;

import edu.hm.peslalz.thesis.statisticservice.entity.FollowedUser;
import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface FollowedUserRepository extends JpaRepository<FollowedUser, Integer> {
    @Query("""
            select fu.userId as identifier, count(interactions) as trendPoints
            from FollowedUser fu
            inner join fu.interactions interactions
            where interactions.registeredTimestamp > :oldestAllowedDate
            GROUP BY fu.userId
            ORDER BY COUNT(interactions) DESC
            """)
    Page<TrendInterface> findByMostInteractionsAfter(Date oldestAllowedDate, Pageable pageable);

    @EntityGraph(attributePaths = "interactions")
    Optional<FollowedUser> findByUserId(int userId);
}

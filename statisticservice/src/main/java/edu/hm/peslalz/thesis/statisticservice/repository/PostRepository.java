package edu.hm.peslalz.thesis.statisticservice.repository;

import edu.hm.peslalz.thesis.statisticservice.entity.Post;
import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("""
            select p.postId as identifier, count(interactions) as trendPoints
            from Post p
            inner join p.interactions interactions
            where interactions.registeredTimestamp > :oldestAllowedDate
            GROUP BY p.postId
            ORDER BY COUNT(interactions) DESC
            """)
    Page<TrendInterface> findByMostInteractionsAfter(Date oldestAllowedDate, Pageable pageable);

    @EntityGraph(attributePaths = "interactions")
    Optional<Post> findByPostId(int postId);

}

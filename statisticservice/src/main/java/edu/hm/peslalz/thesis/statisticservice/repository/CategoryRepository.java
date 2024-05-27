package edu.hm.peslalz.thesis.statisticservice.repository;

import edu.hm.peslalz.thesis.statisticservice.entity.Category;
import edu.hm.peslalz.thesis.statisticservice.entity.TrendInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("""
            select c.name as identifier, count(interactions) as trendPoints
            from Category c
            inner join c.interactions interactions
            where interactions.registeredTimestamp > :oldestAllowedDate
            GROUP BY c.name
            ORDER BY COUNT(interactions) DESC
            """)
    Page<TrendInterface> findByMostInteractionsAfter(Date oldestAllowedDate, Pageable pageable);

    @EntityGraph(attributePaths = "interactions")
    Optional<Category> findByName(String name);


}

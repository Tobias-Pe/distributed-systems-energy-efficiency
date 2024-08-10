package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> lockAndFindById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"comments", "categories"})
    @NonNull
    Optional<Post> findById(@NonNull Integer id);

    @Transactional
    @Query("select p from Post p left join fetch p.imageData where p.id = ?1")
    Optional<Post> findByIdJoinImage(Integer id);

    @Query("""
            select p.id
            from Post p
            inner join p.categories categories
            where
                (:name is null or categories.name = :name) and
                (:userId is null or p.userId = :userId)
            group by p.id
            """)
    Page<Integer> findAllIDsByCategoryAndUserId(@Nullable String name, @Nullable Integer userId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"comments", "categories"})
    @NonNull
    List<Post> findAllById(@NonNull Iterable<Integer> ids);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            noRetryFor = ResponseStatusException.class,
            maxAttempts = 4,
            backoff = @Backoff(random = true, delay = 400, maxDelay = 1000, multiplier = 1.33)
    )
    @Modifying
    @Query("UPDATE Post p set p.likes = p.likes + 1 WHERE p.id = :id")
    void likePost(@NonNull Integer id);
}

package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Post;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Post p where p.id = ?1")
    Optional<Post> lockAndFindById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"comments", "categories"})
    Optional<Post> findById(Integer id);

    @Transactional
    @Query("select p from Post p left join fetch p.imageData where p.id = ?1")
    Optional<Post> findByIdJoinImage(Integer id);
}

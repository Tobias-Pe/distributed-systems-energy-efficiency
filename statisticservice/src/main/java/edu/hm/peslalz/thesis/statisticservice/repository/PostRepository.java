package edu.hm.peslalz.thesis.statisticservice.repository;

import edu.hm.peslalz.thesis.statisticservice.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @EntityGraph(attributePaths = "interactions")
    Optional<Post> findByPostId(int postId);

}

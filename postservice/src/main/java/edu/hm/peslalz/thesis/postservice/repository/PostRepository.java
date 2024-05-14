package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Comment;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Set<Post> findByUserId(Integer userId);

    Set<Post> findByCategories_NameLike(String name);
}

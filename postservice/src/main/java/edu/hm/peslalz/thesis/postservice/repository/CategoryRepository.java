package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Override
    @EntityGraph(attributePaths = {"posts", "posts.comments"})
    List<Category> findAllById(Iterable<String> strings);

    @Query("select c.name from Category c")
    Page<String> findAllNames(Pageable pageable);

    @EntityGraph(attributePaths = {"posts"})
    Optional<Category> findById(String id);
}

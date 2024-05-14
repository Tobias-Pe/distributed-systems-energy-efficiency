package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Category;
import edu.hm.peslalz.thesis.postservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatergoryRepository extends JpaRepository<Category, String> {
}

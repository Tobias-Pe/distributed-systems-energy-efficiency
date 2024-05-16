package edu.hm.peslalz.thesis.postservice.repository;

import edu.hm.peslalz.thesis.postservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}

package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.entity.*;
import edu.hm.peslalz.thesis.postservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}

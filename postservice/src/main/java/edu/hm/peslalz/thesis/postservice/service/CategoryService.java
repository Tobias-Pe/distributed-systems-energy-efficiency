package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<String> getCategories(@RequestParam(defaultValue = "0") int page) {
        // don't page in memory --> page fetch only id's and then load the joined data
        return categoryRepository.findAllNames(PageRequest.of(page, 50));
    }
}

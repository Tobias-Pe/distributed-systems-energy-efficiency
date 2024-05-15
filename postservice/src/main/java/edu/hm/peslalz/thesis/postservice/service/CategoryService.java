package edu.hm.peslalz.thesis.postservice.service;

import edu.hm.peslalz.thesis.postservice.entity.*;
import edu.hm.peslalz.thesis.postservice.repository.CatergoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    CatergoryRepository catergoryRepository;

    public CategoryService(CatergoryRepository catergoryRepository) {
        this.catergoryRepository = catergoryRepository;
    }

    public List<Category> getCategories() {
        return catergoryRepository.findAll();
    }
}

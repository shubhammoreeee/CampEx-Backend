package com.swaplio.swaplio_backend.controller;

import com.swaplio.swaplio_backend.model.Category;
import com.swaplio.swaplio_backend.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired private CategoryRepository categoryRepository;

    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
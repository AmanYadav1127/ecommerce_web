package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    private CategoryService categoryService;
//    public CategoryController() {
//        categories.add(new Category("Electronics", 1L));
//        categories.add(new Category("Clothing", 2L));
//        categories.add(new Category("Books", 3L));
//    }

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categorie")
    public List<Category> getAllCategory(){
        return categoryService.getAllCategories();
    }
    @PostMapping("/api/public/categorie")
    public String createCategory(@RequestBody Category category){
        categoryService.createCategory(category);
        return "Category added successfully";
    }
    @DeleteMapping("/api/admin/categories/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId)
    {
        String stats=categoryService.deleteCategory(categoryId);
        return stats;
    }

}

package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
//private ModelMapper modelMapper;
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }

//    @GetMapping("/echo")
//    public ResponseEntity<String>echoMessage(@RequestParam(name = "message",required = false) String message){
//        return new ResponseEntity<>("Echoed message:"+message, HttpStatus.OK);
//    }

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategory(@RequestParam(name="pageNumber", defaultValue= AppConstants.DEFAULT_PAGE_NUMBER,required = false)Integer pageNumber,
                                                           @RequestParam(name="pageSize", defaultValue=AppConstants.DEFAULT_PAGE_SIZE,required = false)Integer pageSize)
    {
        CategoryResponse categoryResponse=categoryService.getAllCategories(pageNumber,pageSize);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }
    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid  @RequestBody CategoryDto categoryDto){
        CategoryDto savedCategoryDto=categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);
    }
    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId)
    {
            CategoryDto deletedCategory=categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }
    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long categoryId){
            CategoryDto savedCategoryDto=categoryService.updateCategory(categoryDto, categoryId);
            return new ResponseEntity<>(savedCategoryDto, HttpStatus.OK);
    }

}

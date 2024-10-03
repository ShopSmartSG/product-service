// src/main/java/sg/edu/nus/iss/product_service/controller/CategoryController.java
package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.product_service.dto.CategoryDTO;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.service.CategoryService;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Manage categories in Shopsmart Application")
public class CategoryController {
    private final CategoryService categoryService;
    private final ObjectMapper mapper;

    String message = "Category not found";

    @Autowired
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.mapper = objectMapper;
    }

    @GetMapping
    @Operation(summary = "Retrieve all categories")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories = categoryService.getAllCategories(pageable);
            return ResponseEntity.ok(categories);
        } else {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Retrieve category by Category ID")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException(message);
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update a category")
    public ResponseEntity<?> updateCategory(@PathVariable UUID categoryId, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        if(!categoryId.equals(categoryDTO.getCategoryId())) {
            throw new IllegalArgumentException("Category ID mismatch");
        }
        if (existingCategory == null) {
            throw new ResourceNotFoundException(message);
        }
        existingCategory = mapper.convertValue(categoryDTO, Category.class);
        existingCategory.setCategoryId(categoryId);
        categoryService.saveCategory(existingCategory);
        return  ResponseEntity.ok("Category updated successfully");
    }
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        if (existingCategory == null) {
            throw new ResourceNotFoundException(message);
        }
        categoryService.deleteCategory(existingCategory);
        return ResponseEntity.ok("Category deleted successfully");
    }

}
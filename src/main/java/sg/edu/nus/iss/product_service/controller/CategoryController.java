// src/main/java/sg/edu/nus/iss/product_service/controller/CategoryController.java
package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private static final String CATEGORY_NOT_FOUND = "Category not found";

    String message = "Category not found";

    @Autowired
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.mapper = objectMapper;
    }

    @GetMapping
    @Operation(summary = "Retrieve all categories")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.info("Request received to retrieve all categories. Page: {}, Size: {}", page, size);
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categories = categoryService.getAllCategories(pageable);
            log.info("Retrieved {} categories for page {} with size {}", categories.getTotalElements(), page, size);
            return ResponseEntity.ok(categories);
        } else {
            List<Category> categories = categoryService.getAllCategories();
            log.info("Retrieved {} categories without pagination", categories.size());
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/{category-id}")
    @Operation(summary = "Retrieve category by Category ID")
    public ResponseEntity<?> getCategoryById(@PathVariable(name = "category-id") UUID categoryId) {
        log.info("Request received to retrieve category by ID: {}", categoryId);
        try {
            Category category = categoryService.getCategoryById(categoryId);
            log.info("Category retrieved successfully for ID: {}", categoryId);
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            log.warn("Category not found for ID: {}", categoryId);
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error retrieving category by ID: {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Unable to retrieve category.");
        }
    }

    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        log.info("Request received to create a new category: {}", category);
        try {
            Category createdCategory = categoryService.createCategory(category);
            log.info("Category created successfully: {}", createdCategory);
            return ResponseEntity.ok(createdCategory);
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Unable to create category.");
        }
    }

    @PutMapping("/{category-id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<?> updateCategory(@PathVariable(name = "category-id") UUID categoryId, @Valid @RequestBody CategoryDTO categoryDTO) {
        log.info("Request received to update category with ID: {}", categoryId);
        Category existingCategory = categoryService.getCategoryById(categoryId);
        if(!categoryId.equals(categoryDTO.getCategoryId())) {
            log.warn("Category ID mismatch. Path ID: {}, Body ID: {}", categoryId, categoryDTO.getCategoryId());
            throw new IllegalArgumentException("Category ID mismatch");
        }
        if (existingCategory == null) {
            throw new ResourceNotFoundException(message);
        }
        existingCategory = mapper.convertValue(categoryDTO, Category.class);
        existingCategory.setCategoryId(categoryId);
        categoryService.saveCategory(existingCategory);
        log.info("Category updated successfully for ID: {}", categoryId);
        return  ResponseEntity.ok("Category updated successfully");
    }
    @DeleteMapping("/{category-id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "category-id")  UUID categoryId) {
        log.info("Request received to delete category with ID: {}", categoryId);
        Category existingCategory = categoryService.getCategoryById(categoryId);
        if (existingCategory == null) {
            throw new ResourceNotFoundException(message);
        }
        categoryService.deleteCategory(existingCategory);
        log.info("Category deleted successfully for ID: {}", categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }

}
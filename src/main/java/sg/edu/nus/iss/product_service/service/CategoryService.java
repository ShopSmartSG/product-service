package sg.edu.nus.iss.product_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findByDeletedFalse();
        log.info("Retrieved {} categories.", categories.size());
        return categories;
    }

    public Category getCategoryById(UUID categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        return categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found.", categoryId);
                    return new ResourceNotFoundException("Category not found");
                });
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findByDeletedFalse(pageable);
        log.info("Retrieved {} categories in the current page.", categories.getContent().size());
        return categories;
    }

    @Transactional
    public Category createCategory(Category category) {
        categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(category.getCategoryName())
                .ifPresent(c -> {
                    log.warn("Category with name '{}' already exists.", category.getCategoryName());
                    throw new ResourceNotFoundException("Category already exists");
                });
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getCategoryId());
        return savedCategory;
    }

    @Transactional
    public Category saveCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        log.info("Category saved successfully: {}", savedCategory);
        return savedCategory;
    }

    @Transactional
    public Category deleteCategory(Category category) {
        category.setDeleted(true);
        // don't delete a category if it has products
        if (!productRepository.findByCategory_CategoryIdAndDeletedFalse(category.getCategoryId()).isEmpty()) {
            log.warn("Cannot delete category with ID {} because it has associated products.", category.getCategoryId());
            throw new ResourceNotFoundException("Category has products");
        }
        Category deletedCategory = categoryRepository.save(category);
        log.info("Category with ID {} marked as deleted.", category.getCategoryId());
        return deletedCategory;
    }

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(categoryName)
                .orElseThrow(() -> {
                    log.warn("Category with name '{}' not found.", categoryName);
                    return new ResourceNotFoundException("Category not found");
                });
    }
}





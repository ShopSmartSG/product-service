package sg.edu.nus.iss.product_service.service;

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

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findByDeletedFalse();
    }

    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findByDeletedFalse(pageable);
    }

    @Transactional
    public Category createCategory(Category category) {
        categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(category.getCategoryName())
                .ifPresent(c -> {
                    throw new ResourceNotFoundException("Category already exists");
                });
        return categoryRepository.save(category);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category deleteCategory(Category category) {
        category.setDeleted(true);
        // don't delete a category if it has products
        if (!productRepository.findByCategory_CategoryIdAndDeletedFalse(category.getCategoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Category has products");
        }
        return categoryRepository.save(category);
    }

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}





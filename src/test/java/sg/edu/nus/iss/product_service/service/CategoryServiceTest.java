package sg.edu.nus.iss.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories() {
        Category category = new Category();
        when(categoryRepository.findByDeletedFalse()).thenReturn(Collections.singletonList(category));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(1, categories.size());
        assertEquals(category, categories.get(0));
    }

    @Test
    public void testGetCategoryById() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        when(categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);

        assertEquals(category, result);
    }

    @Test
    public void testGetCategoryByIdNotFound() {
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findByCategoryIdAndDeletedFalse(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });
    }

    @Test
    public void testGetAllCategoriesWithPagination() {
        Category category = new Category();
        Page<Category> page = new PageImpl<>(Collections.singletonList(category));
        when(categoryRepository.findByDeletedFalse(any(PageRequest.class))).thenReturn(page);

        Page<Category> result = categoryService.getAllCategories(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(category, result.getContent().get(0));
    }

    @Test
    public void testCreateCategory() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertEquals(category, result);
    }

    @Test
    public void testCreateCategoryAlreadyExists() {
        Category category = new Category();
        when(categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(category.getCategoryName()))
                .thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.createCategory(category);
        });
    }

    @Test
    public void testSaveCategory() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.saveCategory(category);

        assertEquals(category, result);
    }

    @Test
    public void testDeleteCategory() {
        Category category = new Category();
        category.setCategoryId(UUID.randomUUID());
        when(productRepository.findByCategory_CategoryIdAndDeletedFalse(category.getCategoryId()))
                .thenReturn(Collections.emptyList());
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.deleteCategory(category);

        assertTrue(result.isDeleted());
    }

    @Test
    public void testDeleteCategoryWithProducts() {
        Category category = new Category();
        category.setCategoryId(UUID.randomUUID());
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.findByCategory_CategoryIdAndDeletedFalse(category.getCategoryId()))
                .thenReturn(Collections.singletonList(product));

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(category);
        });
    }

    @Test
    public void testGetCategoryByName() {
        String categoryName = "TestCategory";
        Category category = new Category();
        when(categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(categoryName))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryByName(categoryName);

        assertEquals(category, result);
    }

    @Test
    public void testGetCategoryByNameNotFound() {
        String categoryName = "NonExistentCategory";
        when(categoryRepository.findByCategoryNameIgnoreCaseAndDeletedFalse(categoryName))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryByName(categoryName);
        });
    }
}
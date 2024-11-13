package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.product_service.dto.CategoryDTO;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<?> response = categoryController.getAllCategories(null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categories, response.getBody());
    }

    @Test
    public void testGetCategoryById() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<?> response = categoryController.getCategoryById(categoryId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(category, response.getBody());
    }

    @Test
    public void testCreateCategory() {
        Category category = new Category();
        when(categoryService.createCategory(category)).thenReturn(category);

        ResponseEntity<?> response = categoryController.createCategory(category);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(category, response.getBody());
    }

    @Test
    public void testUpdateCategory() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(categoryId);
        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        when(mapper.convertValue(categoryDTO, Category.class)).thenReturn(category);
        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Category updated successfully", response.getBody());
    }

    @Test
    public void testDeleteCategory() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setCategoryId(categoryId);
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<String> response = categoryController.deleteCategory(categoryId);
        when(categoryService.deleteCategory(category)).thenReturn(category);
        assertEquals("Category deleted successfully", response.getBody());
    }

    @Test
    public void testUpdateCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(categoryId);
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryController.updateCategory(categoryId, categoryDTO);
        });

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testDeleteCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryController.deleteCategory(categoryId);
        });

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testGetAllCategories_NoPagination() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<?> response = categoryController.getAllCategories(null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categories, response.getBody());
    }

    @Test
    public void testGetAllCategories_WithPagination() {
        Page<Category> categoryPage = new PageImpl<>(Arrays.asList(new Category()));
        Pageable pageable = PageRequest.of(0, 5);
        when(categoryService.getAllCategories(pageable)).thenReturn(categoryPage);

        ResponseEntity<?> response = categoryController.getAllCategories(0, 5);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categoryPage, response.getBody());
    }

    @Test
    public void testGetCategoryById_Success() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<?> response = categoryController.getCategoryById(categoryId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(category, response.getBody());
    }

    @Test
    public void testUpdateCategory_Success() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(categoryId);

        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(mapper.convertValue(categoryDTO, Category.class)).thenReturn(category);

        ResponseEntity<?> response = categoryController.updateCategory(categoryId, categoryDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Category updated successfully", response.getBody());
        verify(categoryService).saveCategory(category);
    }

    @Test
    public void testUpdateCategory_CategoryIdMismatch() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(UUID.randomUUID()); // Different ID

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryController.updateCategory(categoryId, categoryDTO);
        });

        assertEquals("Category ID mismatch", exception.getMessage());
    }

    @Test
    public void testUpdateCategory_NotFound() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(categoryId);

        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryController.updateCategory(categoryId, categoryDTO);
        });

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testDeleteCategory_Success() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setCategoryId(categoryId);

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<String> response = categoryController.deleteCategory(categoryId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Category deleted successfully", response.getBody());
        verify(categoryService).deleteCategory(category);
    }

    @Test
    public void testDeleteCategory_NotFound() {
        UUID categoryId = UUID.randomUUID();
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryController.deleteCategory(categoryId);
        });

        assertEquals("Category not found", exception.getMessage());
    }
}
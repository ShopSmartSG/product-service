package sg.edu.nus.iss.product_service.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.service.strategy.MerchantProductStrategy;
import sg.edu.nus.iss.product_service.service.ProductServiceContext;
import sg.edu.nus.iss.product_service.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MerchantProductControllerTest {



    @InjectMocks
    private MerchantProductController merchantProductController;

    @Mock
    private ProductServiceContext productServiceContext;

    @Mock
    private MerchantProductStrategy merchantProductStrategy;

    @Mock
    private ProductService productService; // Added the mock for ProductService

    @Mock
    private CategoryService categoryService; // Added the CategoryService mock

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Properly initialize the mocks to avoid NullPointerException
        doNothing().when(productServiceContext).setProductStrategy(anyString());
        when(productServiceContext.getProductStrategy()).thenReturn(merchantProductStrategy);
    }

    @Test
    void testAddProduct() {
        // Mock data
        Product product = new Product();
        product.setProductId(UUID.randomUUID());

        // Set up a non-null Category object to avoid NullPointerException
        Category category = new Category();
        category.setCategoryName("Electronics");
        product.setCategory(category);

        // Mock the behavior of CategoryService to return the category object when called
        when(categoryService.getCategoryByName("Electronics")).thenReturn(category);

        // Mock behavior of adding a product
        when(merchantProductStrategy.addProduct(product)).thenReturn(product);

        // Call the API
        ResponseEntity<?> response = merchantProductController.addProduct(product);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(merchantProductStrategy, times(1)).addProduct(product);
    }


    @Test
    void testDeleteProduct() {
        // Mock data
        UUID productId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(new Product());

        // Mock behavior to avoid NullPointerException
        doNothing().when(merchantProductStrategy).deleteProduct(productId);

        // Call the API
        ResponseEntity<String> response = merchantProductController.deleteProduct(merchantId, productId);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted", response.getBody());
        verify(merchantProductStrategy, times(1)).deleteProduct(productId);
    }
}
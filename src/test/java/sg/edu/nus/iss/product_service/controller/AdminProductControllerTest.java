package sg.edu.nus.iss.product_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.strategy.AdminProductStrategy;
import sg.edu.nus.iss.product_service.service.strategy.ProductServiceContext;
import sg.edu.nus.iss.product_service.dto.ProductDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminProductControllerTest {

    @InjectMocks
    private AdminProductController adminProductController;

    @Mock
    private ProductServiceContext productServiceContext;

    @Mock
    private AdminProductStrategy adminProductStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(productServiceContext.getProductStrategy()).thenReturn(adminProductStrategy);
    }

    @Test
    void testAddProduct() {
        // Mock data
        Product product = new Product();
        product.setProductId(UUID.randomUUID());

        // Mock behavior
        when(adminProductStrategy.addProduct(product)).thenReturn(product);

        // Call the API
        ResponseEntity<?> response = adminProductController.addProduct(product);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(adminProductStrategy, times(1)).addProduct(product);
    }

    @Test
    void testUpdateProduct() {
        // Mock data
        UUID productId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        ProductDTO productDTO = new ProductDTO(productId,merchantId);

        // Mock behavior
        when(adminProductStrategy.updateProduct(productId, product)).thenReturn(product);

        // Call the API
        ResponseEntity<?> response = adminProductController.updateProduct(productId, product);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminProductStrategy, times(1)).updateProduct(any(UUID.class), any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        // Mock data
        UUID productId = UUID.randomUUID();

        // Call the API
        ResponseEntity<String> response = (ResponseEntity<String>) adminProductController.deleteProduct(productId);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted successfully", response.getBody());
        verify(adminProductStrategy, times(1)).deleteProduct(productId);
    }
}
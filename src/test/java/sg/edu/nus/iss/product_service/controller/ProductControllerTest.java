package sg.edu.nus.iss.product_service.controller;

import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    public List<Product> products;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample products for testing
        products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setProductName("Product 1");
        product1.setPincode("12345");
        product1.setMerchantId(UUID.randomUUID());
        product1.setOriginalPrice(new BigDecimal("100.00"));
        product1.setAvailableStock(10);
        products.add(product1);
    }

    @Test
    void testFilterProducts() {
        // Arrange
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setMinPrice(new BigDecimal("50.00"));

        when(productService.getFilteredProducts(filterDTO)).thenReturn(products);

        // Act
        ResponseEntity<List<Product>> response = productController.filterProducts(filterDTO);

        // Assert
        //assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Product 1", response.getBody().getFirst().getProductName());

        // Verify that the service was called once
        verify(productService, times(1)).getFilteredProducts(filterDTO);
    }


}

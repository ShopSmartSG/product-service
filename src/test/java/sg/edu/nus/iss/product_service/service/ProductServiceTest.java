package sg.edu.nus.iss.product_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.deleteProduct(product);

        assertTrue(result.isDeleted());
        assertEquals("merchant", result.getUpdatedBy());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.updateProduct(product);

        assertEquals("merchant", result.getUpdatedBy());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    public void testAddProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addProduct(product);

        assertEquals("merchant", result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void testGetAllProducts() {
        UUID merchantId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByMerchantIdAndDeletedFalse(merchantId, pageable)).thenReturn(page);

        Page<Product> result = productService.getAllProducts(merchantId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(product, result.getContent().get(0));
    }

    @Test
    public void testGetProductsByMerchantId() {
        UUID merchantId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.findByMerchantIdAndDeletedFalse(merchantId)).thenReturn(Collections.singletonList(product));

        List<Product> result = productService.getProductsByMerchantId(merchantId);

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    public void testGetProductsByMerchantIdAndCategoryId() {
        UUID merchantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        when(productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId)).thenReturn(Collections.singletonList(product));

        List<Product> result = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId);

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    public void testGetProductByIdAndMerchantId() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        when(productRepository.findByMerchantIdAndProductIdAndDeletedFalse(merchantId, productId)).thenReturn(product);

        Product result = productService.getProductByIdAndMerchantId(merchantId, productId);

        assertEquals(product, result);
    }

    @Test
    public void testGetFilteredProducts_WithPincode() {
        // Given
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setPincode("12345");
        product1.setOriginalPrice(BigDecimal.valueOf(10.00));
        product1.setListingPrice(BigDecimal.valueOf(8.00));

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setPincode("12345");
        product2.setOriginalPrice(BigDecimal.valueOf(20.00));
        product2.setListingPrice(BigDecimal.valueOf(18.00));

        Product product3 = new Product();
        product3.setProductId(UUID.randomUUID());
        product3.setPincode("54321");
        product3.setOriginalPrice(BigDecimal.valueOf(30.00));
        product3.setListingPrice(BigDecimal.valueOf(28.00));

        // Ensure that the repository returns the correct products
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));

        // Set up filter DTO
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setPincode("12345"); // The pincode you want to filter by

        // When
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        // Then
        assertEquals(2, filteredProducts.size());
    }

    @Test
    public void testGetFilteredProducts_WithCategory() {
        // Given
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setCategoryId(categoryId);

        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setCategory(category);
        product1.setOriginalPrice(BigDecimal.valueOf(10.00));
        product1.setListingPrice(BigDecimal.valueOf(8.00));

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setCategory(category);
        product2.setOriginalPrice(BigDecimal.valueOf(20.00));
        product2.setListingPrice(BigDecimal.valueOf(18.00));

        // Ensure that the repository returns the correct products
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Set up filter DTO
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setCategoryId(categoryId);

        // When
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        // Then
        assertEquals(2, filteredProducts.size());
    }

    @Test
    public void testGetFilteredProducts_WithPriceRange() {
        // Given
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setPincode("12345");
        product1.setOriginalPrice(BigDecimal.valueOf(10.00));
        product1.setListingPrice(BigDecimal.valueOf(8.00));

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setPincode("12345");
        product2.setOriginalPrice(BigDecimal.valueOf(20.00));
        product2.setListingPrice(BigDecimal.valueOf(18.00));

        Product product3 = new Product();
        product3.setProductId(UUID.randomUUID());
        product3.setPincode("54321");
        product3.setOriginalPrice(BigDecimal.valueOf(30.00));
        product3.setListingPrice(BigDecimal.valueOf(28.00));

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));

        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setMinPrice(BigDecimal.valueOf(15.00));
        filterDTO.setMaxPrice(BigDecimal.valueOf(25.00));

        // When
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        // Then
        assertEquals(1, filteredProducts.size()); // Only product2 should match
    }

    @Test
    public void testGetFilteredProducts_WithFullTextSearch_WithoutMock() {
        // Given
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setProductName("Amazing Product");

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setProductName("Awesome Product");

        Product product3 = new Product();
        product3.setProductId(UUID.randomUUID());
        product3.setProductName("Ordinary Item");

        // Mock the repository to return the products
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));

        // Mock the similarity scores
        when(productRepository.similarity("Amazing Product", "Amazing")).thenReturn(0.9);
        when(productRepository.similarity("Awesome Product", "Amazing")).thenReturn(0.3);
        when(productRepository.similarity("Ordinary Item", "Amazing")).thenReturn(0.1);

        // Set up filter DTO
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setSearchText("Amazing");

        // When
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        // Then
        assertEquals(1, filteredProducts.size());
        assertEquals("Amazing Product", filteredProducts.get(0).getProductName());
    }
}

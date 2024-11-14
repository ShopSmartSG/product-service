package sg.edu.nus.iss.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.LatLng;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sg.edu.nus.iss.product_service.service.strategy.FilterStrategy;
import sg.edu.nus.iss.product_service.service.strategy.LocationFilterStrategy;

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

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ExternalLocationService locationService;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
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
        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product1, product2));

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

        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product1, product2, product3));

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

        // Mock the repository to return products matching the search
        when(productRepository.findSimilarProducts("Amazing", 0.1))
                .thenReturn(Arrays.asList(product1));  // Mock only product1 as matching

        // Set up filter DTO with search text
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setSearchText("Amazing");

        // When
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        // Then
        assertEquals(1, filteredProducts.size());
        assertEquals("Amazing Product", filteredProducts.get(0).getProductName());
    }

    @Test
    public void testGetFilteredProducts_WithLocationFilter() {
        // Given
        LatLng targetCoordinates = new LatLng(1.31901, 103.884983); // Target coordinates
        double rangeInKm = 3.0;

        // Create products with pincodes
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setPincode("123456"); // Pincode for product1

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setPincode("654321"); // Pincode for product2

        // Mock coordinates for product pincodes
        when(locationService.getCoordinatesByPincode("123456"))
                .thenReturn(new LatLng(1.32001, 103.9683)); // Outside range
        when(locationService.getCoordinatesByPincode("654321"))
                .thenReturn(new LatLng(1.320549, 103.873827)); // Within range

        // Mock repository to return all products
        List<Product> allProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(allProducts);

        // Create LocationFilterStrategy instance
        FilterStrategy locationFilterStrategy = new LocationFilterStrategy(targetCoordinates, rangeInKm, locationService);
        // When
        List<Product> filteredProducts = locationFilterStrategy.filter(allProducts);

        // Then
        assertEquals(1, filteredProducts.size());
        assertEquals(product2, filteredProducts.get(0));  // Product1 is within range
    }

    @Test
    // test getProductsByIds method with list of strings
    public void testGetProductsByIds() {
        // Given
        List<String> productIds = Arrays.asList("123e4567-e89b-12d3-a456-426614174000", "123e4567-e89b-12d3-a456-426614174001");
        List<UUID> ids = Arrays.asList(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        Product product1 = new Product();
        product1.setProductId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        Product product2 = new Product();
        product2.setProductId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        when(productRepository.findByProductIdInAndDeletedFalse(ids)).thenReturn(Arrays.asList(product1, product2));

        // When
        ResponseEntity<?> response = productService.getProductsByIds(productIds);
        // response size should be same as that of ids list
        List<Product> products = (List<Product>) response.getBody();
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByMerchantIdAndCategoryId_WithPagination() {
        UUID merchantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId, pageable)).thenReturn(page);

        Page<Product> result = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(product, result.getContent().get(0));
    }

    @Test
    public void testGetFilteredProducts_WithMultipleFilters() {
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

        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product1, product2));

        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setCategoryId(categoryId);
        filterDTO.setMinPrice(BigDecimal.valueOf(5.00));
        filterDTO.setMaxPrice(BigDecimal.valueOf(15.00));

        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        assertEquals(1, filteredProducts.size());
        assertEquals(product1, filteredProducts.get(0));
    }

    @Test
    public void testGetFilteredProducts_NoFilters() {
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID());
        product1.setOriginalPrice(BigDecimal.valueOf(10.00));
        product1.setListingPrice(BigDecimal.valueOf(8.00));

        Product product2 = new Product();
        product2.setProductId(UUID.randomUUID());
        product2.setOriginalPrice(BigDecimal.valueOf(20.00));
        product2.setListingPrice(BigDecimal.valueOf(18.00));

        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product1, product2));

        ProductFilterDTO filterDTO = new ProductFilterDTO();

        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);

        assertEquals(2, filteredProducts.size());
    }

    @Test
    public void testGetProductsByIds_InvalidUUIDs() {
        List<String> productIds = Arrays.asList("invalid-uuid-1", "invalid-uuid-2");

        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByIds(productIds);
        });
    }
}

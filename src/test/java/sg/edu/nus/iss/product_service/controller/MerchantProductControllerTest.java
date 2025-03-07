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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.service.ProductServiceContext;
import sg.edu.nus.iss.product_service.service.strategy.MerchantProductStrategy;
import sg.edu.nus.iss.product_service.utility.CloudStorageUtility;
import sg.edu.nus.iss.product_service.dto.ProductDTO;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MerchantProductControllerTest {

    @InjectMocks
    private MerchantProductController merchantProductController;

    @Mock
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CloudStorageUtility cloudStorageUtility;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductServiceContext productServiceContext;

    @Mock
    private MerchantProductStrategy merchantProductStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doNothing().when(productServiceContext).setProductStrategy(anyString());
        when(productServiceContext.getProductStrategy()).thenReturn(merchantProductStrategy);

    }

    @Test
    void testGetAllProducts() {
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = Arrays.asList(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(productList);

        when(productService.getAllProducts(merchantId, pageable)).thenReturn(productPage);

        ResponseEntity<?> response = merchantProductController.getAllProducts("550e8400-e29b-41d4-a716-446655440000", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService, times(1)).getAllProducts(merchantId, pageable);
    }

    @Test
    void testGetProductById() {
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.getProductById("550e8400-e29b-41d4-a716-446655440000", productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductByIdAndMerchantId(merchantId, productId);
    }

    @Test
    void testGetProductById_NotFound() {
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID productId = UUID.randomUUID();

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.getProductById("550e8400-e29b-41d4-a716-446655440000", productId);
        });

        verify(productService, times(1)).getProductByIdAndMerchantId(merchantId, productId);
    }

    @Test
    void testGetProductByMerchantIdAndCategoryId() {
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID categoryId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = Arrays.asList(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(productList);

        when(productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable)).thenReturn(productPage);

        ResponseEntity<?> response = merchantProductController.getProductByMerchantIdAndCategoryId("550e8400-e29b-41d4-a716-446655440000", categoryId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService, times(1)).getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);
    }

    @Test
    void testAddProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        Category category = new Category();
        category.setCategoryName("Electronics");
        product.setCategory(category);

        when(categoryService.getCategoryByName("Electronics")).thenReturn(category);
        when(merchantProductStrategy.addProduct(product)).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.addProduct("550e8400-e29b-41d4-a716-446655440000", product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(merchantProductStrategy, times(1)).addProduct(product);
    }

    @Test
    void testUploadImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "test.jpg";
        String fileUrl = "http://example.com/test.jpg";

        when(cloudStorageUtility.uploadFile(file)).thenReturn(fileName);
        when(cloudStorageUtility.getFileUrl(fileName)).thenReturn(fileUrl);

        ResponseEntity<String> response = merchantProductController.uploadImage(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileUrl, response.getBody());
        verify(cloudStorageUtility, times(1)).uploadFile(file);
        verify(cloudStorageUtility, times(1)).getFileUrl(fileName);
    }

    @Test
    void testDeleteProduct() {
        UUID productId = UUID.randomUUID();
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(new Product());
        doNothing().when(merchantProductStrategy).deleteProduct(productId);

        ResponseEntity<String> response = merchantProductController.deleteProduct("550e8400-e29b-41d4-a716-446655440000", productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted", response.getBody());
        verify(merchantProductStrategy, times(1)).deleteProduct(productId);
    }

    @Test
    void testGetAllProducts_NoPagination() {
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        List<Product> productList = Arrays.asList(new Product(), new Product());

        when(productService.getProductsByMerchantId(merchantId)).thenReturn(productList);

        ResponseEntity<?> response = merchantProductController.getAllProducts("550e8400-e29b-41d4-a716-446655440000", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productList, response.getBody());
        verify(productService, times(1)).getProductsByMerchantId(merchantId);
    }

    @Test
    void testAddProduct_NonExistentCategory() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        Category category = new Category();
        category.setCategoryName("NonExistentCategory");
        product.setCategory(category);

        when(categoryService.getCategoryByName("NonExistentCategory")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.addProduct("550e8400-e29b-41d4-a716-446655440000", product);
        });

        verify(categoryService, times(1)).getCategoryByName("NonExistentCategory");
        verify(merchantProductStrategy, never()).addProduct(product);
    }

    @Test
    void testUpdateProduct() {
        UUID categoryId = UUID.randomUUID();
        UUID merchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO(categoryId,merchantId);
        productDTO.setProductId(productId);
        productDTO.setCategoryId(UUID.randomUUID());
        Product product = new Product();
        product.setProductId(productId);

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);
        when(objectMapper.convertValue(productDTO, Product.class)).thenReturn(product);
        when(categoryService.getCategoryById(productDTO.getCategoryId())).thenReturn(new Category());
        when(merchantProductStrategy.updateProduct(productId, product)).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.updateProduct("550e8400-e29b-41d4-a716-446655440000", productId, productDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(merchantProductStrategy, times(1)).updateProduct(productId, product);
    }
}
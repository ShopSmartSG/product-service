package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.dto.ProductDTO;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.utility.S3Utility;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MerchantProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private S3Utility s3Service;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private MerchantProductController merchantProductController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        UUID merchantId = UUID.randomUUID();
        List<Product> products = List.of(new Product());
        when(productService.getProductsByMerchantId(merchantId)).thenReturn(products);

        ResponseEntity<?> response = merchantProductController.getAllProducts(merchantId, null, null);

        assertEquals(ResponseEntity.ok(products), response);
    }

    @Test
    void testGetProductById() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.getProductById(merchantId, productId);

        assertEquals(ResponseEntity.ok(product), response);
    }

    @Test
    void testGetProductById_NotFound() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.getProductById(merchantId, productId);
        });
    }

    @Test
    void testGetProductByMerchantIdAndCategoryId() {
        UUID merchantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        List<Product> products = List.of(new Product());
        when(productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId)).thenReturn(products);

        ResponseEntity<?> response = merchantProductController.getProductByMerchantIdAndCategoryId(merchantId, categoryId, null, null);

        assertEquals(ResponseEntity.ok(products), response);
    }

    @Test
    void testAddProduct() {
        Product product = new Product();
        Category category = new Category();
        category.setCategoryName("Test Category");
        product.setCategory(category);
        when(categoryService.getCategoryByName(any())).thenReturn(category);
        when(productService.addProduct(any())).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.addProduct(product);

        assertEquals(ResponseEntity.ok(product), response);
    }

    @Test
    void testAddProduct_CategoryNotFound() {
        Product product = new Product();
        Category category = new Category();
        category.setCategoryName("Test Category");
        product.setCategory(category);
        when(categoryService.getCategoryByName(any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.addProduct(product);
        });
    }

    @Test
    void testUploadImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "test.jpg";
        String fileUrl = "http://example.com/test.jpg";
        when(s3Service.uploadFile(file)).thenReturn(fileName);
        when(s3Service.getFileUrl(fileName)).thenReturn(fileUrl);

        ResponseEntity<String> response = merchantProductController.uploadImage(file);

        assertEquals(ResponseEntity.ok(fileUrl), response);
    }

    @Test
    void testDeleteProduct() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);

        ResponseEntity<String> response = merchantProductController.deleteProduct(merchantId, productId);

        assertEquals(ResponseEntity.ok("Delete: successful"), response);
    }

    @Test
    void testDeleteProduct_NotFound() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.deleteProduct(merchantId, productId);
        });
    }

    @Test
    void testUpdateProduct() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductDTO dto = new ProductDTO(categoryId,merchantId);
        dto.setProductId(productId);
        Product product = new Product();
        product.setProductId(productId);
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);
        when(objectMapper.convertValue(dto, Product.class)).thenReturn(product);
        when(categoryService.getCategoryById(any())).thenReturn(new Category());
        when(productService.updateProduct(any())).thenReturn(product);

        ResponseEntity<?> response = merchantProductController.updateProduct(merchantId, productId, dto,product);

        assertEquals(ResponseEntity.ok(product), response);
    }

    @Test
    void testUpdateProduct_NotFound() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductDTO dto = new ProductDTO(categoryId,merchantId);
        Product product = new Product();
        dto.setProductId(productId);
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            merchantProductController.updateProduct(merchantId, productId, dto,product);
        });
    }

    @Test
    void testUpdateProduct_IdMismatch() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductDTO dto = new ProductDTO(categoryId,merchantId);
        dto.setProductId(UUID.randomUUID());
        Product product = new Product();
        product.setProductId(productId);
        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);

        assertThrows(IllegalArgumentException.class, () -> {
            merchantProductController.updateProduct(merchantId, productId, dto,product);
        });
    }
}
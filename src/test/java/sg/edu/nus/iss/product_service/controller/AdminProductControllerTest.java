package sg.edu.nus.iss.product_service.controller;

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
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.ProductServiceContext;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.strategy.AdminProductStrategy;
import sg.edu.nus.iss.product_service.utility.S3Utility;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminProductControllerTest {

    @InjectMocks
    private AdminProductController adminProductController;

    @Mock
    private ProductService productService;

    @Mock
    private S3Utility s3Service;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductServiceContext productServiceContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        UUID merchantId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = Arrays.asList(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(productList);

        when(productService.getAllProducts(merchantId, pageable)).thenReturn(productPage);

        ResponseEntity<?> response = adminProductController.getAllProducts(merchantId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService, times(1)).getAllProducts(merchantId, pageable);
    }

    @Test
    void testGetProductById() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(product);

        ResponseEntity<?> response = adminProductController.getProductById(merchantId, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductByIdAndMerchantId(merchantId, productId);
    }

    @Test
    void testGetProductById_NotFound() {
        UUID merchantId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(productService.getProductByIdAndMerchantId(merchantId, productId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            adminProductController.getProductById(merchantId, productId);
        });

        verify(productService, times(1)).getProductByIdAndMerchantId(merchantId, productId);
    }

    @Test
    void testAddProduct() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());

        when(productServiceContext.getProductStrategy()).thenReturn(mock(AdminProductStrategy.class));
        when(productServiceContext.getProductStrategy().addProduct(product)).thenReturn(product);

        ResponseEntity<?> response = adminProductController.addProduct(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productServiceContext.getProductStrategy(), times(1)).addProduct(product);
    }

    @Test
    void testUploadImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "test.jpg";
        String fileUrl = "http://example.com/test.jpg";

        when(s3Service.uploadFile(file)).thenReturn(fileName);
        when(s3Service.getFileUrl(fileName)).thenReturn(fileUrl);

        ResponseEntity<String> response = adminProductController.uploadImage(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileUrl, response.getBody());
        verify(s3Service, times(1)).uploadFile(file);
        verify(s3Service, times(1)).getFileUrl(fileName);
    }

    @Test
    void testDeleteProduct() {
        UUID productId = UUID.randomUUID();
        AdminProductStrategy adminProductStrategy = mock(AdminProductStrategy.class);

        when(productServiceContext.getProductStrategy()).thenReturn(adminProductStrategy);
        doNothing().when(adminProductStrategy).deleteProduct(productId);

        ResponseEntity<?> response = adminProductController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted", response.getBody());
        verify(adminProductStrategy, times(1)).deleteProduct(productId);
    }

    @Test
    void testUpdateProduct() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        when(productServiceContext.getProductStrategy()).thenReturn(mock(AdminProductStrategy.class));
        when(productServiceContext.getProductStrategy().updateProduct(productId, product)).thenReturn(product);

        ResponseEntity<?> response = adminProductController.updateProduct(productId, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productServiceContext.getProductStrategy(), times(1)).updateProduct(productId, product);
    }


    @Test
    void testGetAllProducts_NoPagination() {
        UUID merchantId = UUID.randomUUID();
        List<Product> productList = Arrays.asList(new Product(), new Product());

        when(productService.getProductsByMerchantId(merchantId)).thenReturn(productList);

        ResponseEntity<?> response = adminProductController.getAllProducts(merchantId, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productList, response.getBody());
        verify(productService, times(1)).getProductsByMerchantId(merchantId);
    }

    @Test
    void testGetProductByMerchantIdAndCategoryId_NoPagination() {
        UUID merchantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        List<Product> productList = Arrays.asList(new Product(), new Product());

        when(productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId)).thenReturn(productList);

        ResponseEntity<?> response = adminProductController.getProductByMerchantIdAndCategoryId(merchantId, categoryId, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productList, response.getBody());
        verify(productService, times(1)).getProductsByMerchantIdAndCategoryId(merchantId, categoryId);
    }

    @Test
    void testAddProduct_InvalidProduct() {
        Product product = new Product();
        product.setProductId(null); // Invalid product ID

        when(productServiceContext.getProductStrategy()).thenReturn(mock(AdminProductStrategy.class));
        when(productServiceContext.getProductStrategy().addProduct(product)).thenThrow(new IllegalArgumentException("Invalid product ID"));

        assertThrows(IllegalArgumentException.class, () -> {
            adminProductController.addProduct(product);
        });

        verify(productServiceContext.getProductStrategy(), times(1)).addProduct(product);
    }

    @Test
    void testUploadImage_InvalidFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<String> response = adminProductController.uploadImage(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
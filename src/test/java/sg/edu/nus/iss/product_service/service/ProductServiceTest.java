
package sg.edu.nus.iss.product_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

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
}
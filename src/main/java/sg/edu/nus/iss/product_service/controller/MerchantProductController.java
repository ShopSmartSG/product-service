package sg.edu.nus.iss.product_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.iss.product_service.dto.ProductDTO;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.utility.S3Utility;
import sg.edu.nus.iss.product_service.service.strategy.ProductServiceContext;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@Tag(name = "Merchant Product API", description = "APIs for merchants to create, read, update, and delete products")
public class MerchantProductController {

    private final ProductService productService;
    private final S3Utility s3Service;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final ProductServiceContext productServiceContext;

    @Autowired
    public MerchantProductController(ProductService productService, ObjectMapper objectMapper, S3Utility s3Service, CategoryService categoryService) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
        this.categoryService = categoryService;
        this.productServiceContext = new ProductServiceContext();
    }

    // Helper method to create pageable object
    private Pageable createPageable(Integer page, Integer size) {
        return (page != null && size != null) ? PageRequest.of(page, size) : Pageable.unpaged();
    }

    // Helper method to validate product existence
    private Product validateProduct(UUID merchantId, UUID productId) {
        Product product = productService.getProductByIdAndMerchantId(merchantId, productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return product;
    }

    // Helper method to set the product strategy to merchant
    private void setMerchantProductStrategy() {
        productServiceContext.setProductStrategy("merchant");
    }

    @GetMapping("/{merchantId}/products")
    @Operation(summary = "Retrieve all products")
    public ResponseEntity<?> getAllProducts(@PathVariable UUID merchantId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = createPageable(page, size);
        if (pageable.isPaged()) {
            Page<Product> products = productService.getAllProducts(merchantId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantId(merchantId);
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Retrieve product By ID")
    public ResponseEntity<?> getProductById(@PathVariable UUID merchantId, @PathVariable UUID productId) {
        Product product = validateProduct(merchantId, productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{merchantId}/categories/{categoryId}")
    @Operation(summary = "Retrieve products by merchant ID and category ID")
    public ResponseEntity<?> getProductByMerchantIdAndCategoryId(@PathVariable UUID merchantId, @PathVariable UUID categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = createPageable(page, size);
        if (pageable.isPaged()) {
            Page<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId);
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping("/products")
    @Operation(summary = "Add a new product")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        Category category = categoryService.getCategoryByName(product.getCategory().getCategoryName());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found, Please create category first");
        }
        product.setCategory(category);
        setMerchantProductStrategy();
        return ResponseEntity.ok(productServiceContext.getProductStrategy().addProduct(product));
    }

    @PostMapping("/images/upload")
    @Operation(summary = "Upload a product image", description = "Uploads a product image to S3")
    public ResponseEntity<String> uploadImage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "File to upload",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        String fileName = s3Service.uploadFile(file);
        String fileUrl = s3Service.getFileUrl(fileName);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Delete product by Product ID")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID merchantId, @PathVariable UUID productId) {
        setMerchantProductStrategy();
        Product existingProduct = validateProduct(merchantId, productId);
        productServiceContext.getProductStrategy().deleteProduct(productId);
        return ResponseEntity.ok("Product deleted");
    }

    @PutMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<?> updateProduct(@PathVariable UUID merchantId, @PathVariable UUID productId, @Valid @RequestBody ProductDTO dto, @Valid @RequestBody Product product) {
        setMerchantProductStrategy();
        Product existingProduct = validateProduct(merchantId, productId);
        if (!existingProduct.getProductId().equals(dto.getProductId())) {
            throw new IllegalArgumentException("Product ID mismatch");
        }
        existingProduct = objectMapper.convertValue(dto, Product.class);
        existingProduct.setProductId(productId);
        existingProduct.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        return ResponseEntity.ok(productServiceContext.getProductStrategy().updateProduct(productId, product));
    }
}
package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import sg.edu.nus.iss.product_service.service.ProductServiceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.utility.S3Utility;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Product API", description = "APIs for admin to create, read, update, and delete products")
public class AdminProductController {
    private final ProductService productService;
    private final S3Utility s3Service;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final ProductServiceContext productServiceContext;

    @Autowired
    public AdminProductController(ProductService productService, ObjectMapper objectMapper, S3Utility s3Service, CategoryService categoryService, ProductServiceContext productServiceContext) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
        this.categoryService = categoryService;
        this.productServiceContext= productServiceContext;
    }

    @GetMapping("/products")
    @Operation(summary = "Retrieve all products")
    public ResponseEntity<?> getAllProducts(@RequestParam(required = false) UUID merchantId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProducts(merchantId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantId(merchantId);
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/products/{product-id}")
    @Operation(summary = "Retrieve product By ID")
    public ResponseEntity<?> getProductById(@RequestParam(required = false) UUID merchantId, @PathVariable(name = "product-id") UUID productId) {
        Product product = productService.getProductByIdAndMerchantId(merchantId, productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return ResponseEntity.ok(product);
    }


    @GetMapping("/{merchant-id}/categories/{category-id}")
    @Operation(summary = "Retrieve products by merchant ID and category ID")
    public ResponseEntity<?> getProductByMerchantIdAndCategoryId(@PathVariable(name = "merchant-id") UUID merchantId, @PathVariable(name = "category-id") UUID categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId);
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping("/products")
    @Operation(summary = "Add a new product")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        productServiceContext.setProductStrategy("admin");
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

    @DeleteMapping("/products/{product-id}")
    @Operation(summary = "Delete product by Product ID")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "product-id") UUID productId) {
        productServiceContext.setProductStrategy("admin");
        productServiceContext.getProductStrategy().deleteProduct(productId);
        return ResponseEntity.ok("Product deleted");
    }

    @PutMapping("/{merchant-id}/products/{product-id}")
    @Operation(summary = "Update product")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "product-id") UUID productId, @RequestBody Product product) {
        productServiceContext.setProductStrategy("admin");
        return ResponseEntity.ok(productServiceContext.getProductStrategy().updateProduct(productId, product));
    }
}
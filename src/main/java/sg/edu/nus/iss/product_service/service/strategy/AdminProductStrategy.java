package sg.edu.nus.iss.product_service.service.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class AdminProductStrategy implements ProductStrategy {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product addProduct(Product product) {
        product.setCreatedBy("Admin");
        product.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(UUID productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setProductName(product.getProductName());
        existingProduct.setListingPrice(product.getListingPrice());
        existingProduct.setUpdatedBy("Admin");
        existingProduct.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setDeleted(true);
        existingProduct.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        productRepository.save(existingProduct);
    }

}
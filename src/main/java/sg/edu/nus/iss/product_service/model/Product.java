package sg.edu.nus.iss.product_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import sg.edu.nus.iss.product_service.model.Category;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID productId;
    private String productName;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    private String imageUrl;
    private String productDescription;
    private BigDecimal originalPrice;
    private BigDecimal listingPrice;
    private int availableStock;
    private UUID merchantId;
    private String pincode;
    private boolean deleted;
}


package sg.edu.nus.iss.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID productId;
    private String productName;
    // small object for transfer
    private UUID categoryId;
    private String imageUrl;
    private String productDescription;
    private BigDecimal originalPrice;
    private BigDecimal listingPrice;
    private int availableStock;
    private String pincode;
    private UUID merchantId;

    @JsonIgnore
    private boolean deleted=false;
}

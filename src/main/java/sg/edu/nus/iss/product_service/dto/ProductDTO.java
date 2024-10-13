package sg.edu.nus.iss.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {

    private UUID productId;
    @NotBlank(message = "Product name is mandatory")
    private String productName;
    // small object for transfer
    @NonNull
    private UUID categoryId;
    @NotBlank(message = "Please provide an image URL")
    private String imageUrl;
    private String productDescription;
    @Positive(message = "Please provide a valid original price")
    private BigDecimal originalPrice;
    @Positive(message = "Please provide a valid listing price")
    private BigDecimal listingPrice;
    @Positive(message = "Please provide a valid available stock")
    private int availableStock;
    private String pincode;
    @NonNull
    private UUID merchantId;

    @JsonIgnore
    private boolean deleted=false;
}

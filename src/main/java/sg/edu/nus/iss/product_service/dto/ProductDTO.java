package sg.edu.nus.iss.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {

    private UUID productId;
    @NotBlank(message = "Product name is mandatory")
    private String productName;
    @NonNull
    private UUID categoryId;
    @NotBlank(message = "Please provide an image URL")
    private String imageUrl;
    private String productDescription;
    @NonNull
    private BigDecimal originalPrice;
    @NonNull
    private BigDecimal listingPrice;
    @NonNull
    private int availableStock;
    @NonNull
    private UUID merchantId;

    @JsonIgnore
    private boolean deleted=false;
}

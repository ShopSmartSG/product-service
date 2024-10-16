package sg.edu.nus.iss.product_service.model;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private UUID cartItemId;

    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
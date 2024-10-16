package sg.edu.nus.iss.product_service.model;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Cart {
    @Id
    @GeneratedValue
    private UUID cartId;

    private UUID userId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    // Total price of items in the cart
    private double totalPrice;
}

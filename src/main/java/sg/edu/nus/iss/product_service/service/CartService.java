package sg.edu.nus.iss.product_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.edu.nus.iss.product_service.model.Cart;
import sg.edu.nus.iss.product_service.model.CartItem;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CartRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart addProductToCart(UUID userId, UUID productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getProductId());
        cartItem.setProductName(product.getProductName());
        cartItem.setPrice(product.getListingPrice());
        cartItem.setQuantity(quantity);

        cart.getCartItems().add(cartItem);
        cart.setTotalPrice(cart.getTotalPrice() + product.getListingPrice().doubleValue() * quantity);

        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(UUID userId, UUID productId) {
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));

        return cartRepository.save(cart);
    }
}
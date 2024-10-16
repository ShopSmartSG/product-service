package sg.edu.nus.iss.product_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.iss.product_service.model.Cart;
import sg.edu.nus.iss.product_service.service.CartService;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId, @RequestParam int quantity) {
        Cart cart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        Cart cart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }
}
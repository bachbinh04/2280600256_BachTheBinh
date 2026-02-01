package com.bachthebinh2280600256.bachthebinh.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bachthebinh2280600256.bachthebinh.services.CartService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String showCart(HttpSession session,
            @NotNull Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice",
                cartService.getSumPrice(session));
        model.addAttribute("totalQuantity",
                cartService.getSumQuantity(session));
        return "book/cart";
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session,
            @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItem(id);
        return "redirect:/cart";
    }

    @GetMapping("/updateCart/{id}/{quantity}")
    public String updateCart(HttpSession session,
            @PathVariable Long id,
            @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.updateItem(id, quantity);
        return "book/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart ";
    }

    // --- THÊM MỚI: CHECKOUT ---
    @GetMapping("/checkout")
    public String checkout(HttpSession session) {
        // Vì chưa có bảng Order trong Database, 
        // ta sẽ xử lý đơn giản: Xóa giỏ hàng và thông báo thành công.

        // 1. (Tại đây có thể thêm code lưu Order vào CSDL nếu có Entity Order)
        // 2. Xóa giỏ hàng sau khi checkout
        cartService.removeCart(session);

        // 3. Chuyển hướng đến trang thông báo hoặc trang chủ
        return "redirect:/cart/success";
        // Hoặc return "redirect:/books"; nếu không muốn làm trang success riêng
    }

    // Trang thông báo đặt hàng thành công (Tùy chọn)
    @GetMapping("/success")
    public String checkoutSuccess() {
        return "book/checkout_success"; // Cần tạo file templates/book/checkout_success.html
    }
}

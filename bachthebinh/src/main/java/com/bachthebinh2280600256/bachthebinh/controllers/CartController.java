package com.bachthebinh2280600256.bachthebinh.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bachthebinh2280600256.bachthebinh.daos.Cart;
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.services.CartService;
import com.bachthebinh2280600256.bachthebinh.services.OrderService;
import com.bachthebinh2280600256.bachthebinh.services.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public String showCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getSumPrice(session));
        model.addAttribute("totalQuantity", cartService.getSumQuantity(session));
        return "book/cart";
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session, @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItem(id);
        return "redirect:/cart";
    }

   @GetMapping("/updateCart/{id}/{quantity}")
    @ResponseBody // <--- QUAN TRỌNG: Để trả về JSON cho Javascript
    public Map<String, Object> updateCart(HttpSession session,
            @PathVariable Long id,
            @PathVariable int quantity) {
        
        var cart = cartService.getCart(session);
        cart.updateItem(id, quantity);

        double totalPrice = cartService.getSumPrice(session);
        double itemTotal = 0;
        
        // Tính thành tiền của riêng sản phẩm đó
        for (var item : cart.getCartItems()) {
            if (item.getBookId().equals(id)) {
                itemTotal = item.getPrice() * item.getQuantity();
                break;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalPrice", totalPrice);
        response.put("itemTotal", itemTotal);
        return response;
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

    // --- XỬ LÝ THANH TOÁN THƯỜNG (COD) ---
    @GetMapping("/checkout")
    public String checkout(HttpSession session, java.security.Principal principal) {
        // 1. Kiểm tra xem người dùng đã đăng nhập chưa
        if (principal == null) {
            return "redirect:/login"; // Chưa đăng nhập thì bắt đăng nhập
        }

        // 2. Lấy thông tin người dùng hiện tại
        String username = principal.getName();
        User user = userService.findByUsername(username).orElse(null);

        // 3. Gọi hàm lưu đơn hàng (Sử dụng phương thức "COD" - Thanh toán khi nhận hàng)
        if (user != null) {
            orderService.placeOrder(user, "COD", session); 
            // Lưu ý: Hàm placeOrder trong OrderService đã bao gồm lệnh xóa giỏ hàng rồi
            // nên ta không cần gọi cartService.removeCart(session) ở đây nữa.
        }

        // 4. Chuyển hướng đến trang thông báo thành công
        return "redirect:/cart/success"; 
    }

    // Trang thông báo thành công (Cập nhật nhẹ để hiển thị đúng text)
    @GetMapping("/success")
    public String checkoutSuccess(Model model) {
        model.addAttribute("message", "Bạn đã đặt hàng thành công (Thanh toán tiền mặt)!");
        model.addAttribute("paymentStatus", "SUCCESS");
        return "book/checkout_success";
    }

 
    // --- LƯU Ý: ĐÃ XÓA HÀM orderHistory ---
    // Hàm đó thuộc về OrderController, không nên để ở đây.
}
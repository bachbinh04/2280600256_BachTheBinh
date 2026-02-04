package com.bachthebinh2280600256.bachthebinh.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.services.CartService;
import com.bachthebinh2280600256.bachthebinh.services.MoMoService;
import com.bachthebinh2280600256.bachthebinh.services.OrderService;
import com.bachthebinh2280600256.bachthebinh.services.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MoMoController {

    private final MoMoService moMoService;
    private final CartService cartService;
    
    // 2. KHAI BÁO CÁC SERVICE CÒN THIẾU Ở ĐÂY:
    private final UserService userService;
    private final OrderService orderService;

    // --- Kích hoạt thanh toán ---
    @PostMapping("/momo/create")
    public String createUrl(HttpSession session) {
        String orderId = String.valueOf(System.currentTimeMillis());
        
        // Lấy tổng tiền và ép kiểu long
        long totalAmount = (long) cartService.getSumPrice(session);
        String amount = String.valueOf(totalAmount);
        String orderInfo = "Thanh toan don hang: " + orderId;

        Map<String, Object> response = moMoService.createPayment(orderId, amount, orderInfo);

        if (response != null && response.containsKey("payUrl")) {
            return "redirect:" + response.get("payUrl");
        } else {
            return "redirect:/cart?error=momo_failed";
        }
    }

    // --- Xử lý kết quả trả về ---
    @GetMapping("/momo/return")
    public String returnCallback(@RequestParam Map<String, String> params, 
                                 Model model, 
                                 HttpSession session,
                                 java.security.Principal principal) { 
        String resultCode = params.get("resultCode");
        String message = params.get("message");

        if ("0".equals(resultCode)) {
            // 1. Lấy User hiện tại
            if (principal != null) {
                String username = principal.getName();
                // Tìm User trong DB
                User user = userService.findByUsername(username).orElse(null); 
                
                // 2. Lưu đơn hàng
                if (user != null) {
                    orderService.placeOrder(user, "MOMO", session);
                }
            }

            model.addAttribute("paymentStatus", "SUCCESS");
            model.addAttribute("message", "Giao dịch thành công! Đơn hàng đã được lưu.");
        } else {
            model.addAttribute("paymentStatus", "FAILED");
            model.addAttribute("message", "Giao dịch thất bại: " + message);
        }

        return "book/checkout_success";
    }
}
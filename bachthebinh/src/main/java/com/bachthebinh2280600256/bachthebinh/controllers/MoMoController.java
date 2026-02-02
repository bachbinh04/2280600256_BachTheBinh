package com.bachthebinh2280600256.bachthebinh.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bachthebinh2280600256.bachthebinh.services.CartService;
import com.bachthebinh2280600256.bachthebinh.services.MoMoService;

import jakarta.servlet.http.HttpSession; // 1. Import HttpSession
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MoMoController {

    private final MoMoService moMoService;
    private final CartService cartService;

    // 1. Kích hoạt thanh toán
    @PostMapping("/momo/create")
    public String createUrl(HttpSession session) { // 2. Thêm tham số HttpSession vào đây
        String orderId = String.valueOf(System.currentTimeMillis());

        // 3. Truyền session vào hàm getSumPrice
// SỬA DÒNG NÀY: Thêm (long) vào trước để ép kiểu
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

    // 2. Xử lý khi thanh toán xong (MoMo gọi về)
    @GetMapping("/momo/return")
    public String returnCallback(@RequestParam Map<String, String> params, Model model) {
        String resultCode = params.get("resultCode");
        String message = params.get("message");

        if ("0".equals(resultCode)) {
            // Thanh toán thành công -> Có thể cần xóa giỏ hàng ở đây
            // cartService.clearCart(session); // (Nếu cần xóa thì nhớ thêm HttpSession vào tham số hàm này luôn)

            model.addAttribute("message", "Thanh toán thành công qua MoMo!");
            return "cart/checkout_success";
        } else {
            model.addAttribute("message", "Thanh toán thất bại: " + message);
            return "cart/checkout_failed";
        }
    }
}

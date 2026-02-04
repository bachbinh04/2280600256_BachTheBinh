package com.bachthebinh2280600256.bachthebinh.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bachthebinh2280600256.bachthebinh.entities.Order;
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.services.OrderService;
import com.bachthebinh2280600256.bachthebinh.services.UserService; // Import bảo mật

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    // 1. CHO USER: Xem lịch sử mua hàng của bản thân
    @GetMapping("/history")
    public String orderHistory(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String username = principal.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order/history";
    }

    // 2. CHO ADMIN: Quản lý tất cả đơn hàng
    @GetMapping("/admin")
    //@PreAuthorize("hasAuthority('ADMIN')") // Bật dòng này nếu đã cấu hình Security
    public String adminOrderList(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/admin_list"; // Tạo file admin_list.html
    }

    // 3. XEM CHI TIẾT ĐƠN HÀNG (Chung cho cả 2)
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order/detail"; // Tạo file detail.html
    }
}
package com.bachthebinh2280600256.bachthebinh.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bachthebinh2280600256.bachthebinh.entities.Order;
import com.bachthebinh2280600256.bachthebinh.entities.OrderDetail;
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.services.OrderService;
import com.bachthebinh2280600256.bachthebinh.services.UserService; // Import bảo mật

import jakarta.servlet.http.HttpServletResponse;
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
    @GetMapping("/export/{id}")
    public void exportOrderToExcel(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        // 1. Lấy thông tin đơn hàng từ Database
        Order order = orderService.getOrderById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        // 2. Cấu hình file trả về là Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=HoaDon_" + order.getId() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // 3. Tạo file Excel mới
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Chi Tiết Hóa Đơn");

            // Tạo các dòng thông tin chung
            Row row0 = sheet.createRow(0);
            row0.createCell(0).setCellValue("Mã đơn hàng:");
            row0.createCell(1).setCellValue(order.getId());

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Người mua:");
            row1.createCell(1).setCellValue(order.getUser().getUsername());

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Tổng tiền:");
            row2.createCell(1).setCellValue(order.getTotalPrice() + " VNĐ");

            // Để trống 1 dòng
            sheet.createRow(3);

            // Tạo tiêu đề cho bảng chi tiết sách
            Row headerRow = sheet.createRow(4);
            headerRow.createCell(0).setCellValue("Tên sách");
            headerRow.createCell(1).setCellValue("Đơn giá");
            headerRow.createCell(2).setCellValue("Số lượng");
            headerRow.createCell(3).setCellValue("Thành tiền");

            // Đổ dữ liệu các cuốn sách vào bảng
            int rowCount = 5;
            for (OrderDetail detail : order.getOrderDetails()) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(detail.getBook().getTitle());
                row.createCell(1).setCellValue(detail.getPrice());
                row.createCell(2).setCellValue(detail.getQuantity());
                row.createCell(3).setCellValue(detail.getPrice() * detail.getQuantity());
            }

            // Tự động căn chỉnh độ rộng các cột cho đẹp
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file ra response để trình duyệt tải về
            workbook.write(response.getOutputStream());
        }
    }
}
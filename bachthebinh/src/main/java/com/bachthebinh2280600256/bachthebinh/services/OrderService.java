package com.bachthebinh2280600256.bachthebinh.services;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bachthebinh2280600256.bachthebinh.daos.Cart;
import com.bachthebinh2280600256.bachthebinh.daos.Item;
import com.bachthebinh2280600256.bachthebinh.entities.Book;
import com.bachthebinh2280600256.bachthebinh.entities.Order;
import com.bachthebinh2280600256.bachthebinh.entities.OrderDetail; // <--- THÊM DÒNG NÀY
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.repositories.IBookRepository;
import com.bachthebinh2280600256.bachthebinh.repositories.IOrderDetailRepository;
import com.bachthebinh2280600256.bachthebinh.repositories.IOrderRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IBookRepository bookRepository;
    private final CartService cartService;

    // Hàm xử lý lưu đơn hàng
    public void placeOrder(User user, String paymentMethod, HttpSession session) {
        // 1. Lấy giỏ hàng từ Session
        Cart cart = cartService.getCart(session);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return; // Giỏ hàng rỗng thì không làm gì
        }

        // 2. Tạo đối tượng Order (Đơn hàng cha)
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date()); // Ngày hiện tại
        order.setPaymentMethod(paymentMethod); // VD: "MOMO", "VNPAY"
        order.setStatus("PAID"); // Trạng thái đã thanh toán
        
        // Lấy tổng tiền (ép kiểu double nếu getSumPrice trả về số khác)
        order.setTotalPrice((double) cartService.getSumPrice(session)); 
        
        // Lưu Order vào DB để lấy ID
        Order savedOrder = orderRepository.save(order);

        // 3. Tạo các OrderDetail (Chi tiết đơn hàng)
        for (Item item : cart.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder); // Gắn vào đơn hàng vừa tạo
            
            // Lấy thông tin sách mới nhất từ DB (để tránh sai giá)
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                detail.setBook(book);
                detail.setQuantity(item.getQuantity());
                detail.setPrice(item.getPrice()); // Giá tại thời điểm mua
                
                orderDetailRepository.save(detail);
            }
        }

        // 4. Xóa giỏ hàng sau khi lưu xong
        cartService.clearCart(session);
    }

    // Lấy danh sách đơn hàng của User
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    // 1. Lấy tất cả đơn hàng (Cho Admin)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    // 2. Lấy đơn hàng theo ID (để xem chi tiết)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
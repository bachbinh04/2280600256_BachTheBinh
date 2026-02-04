package com.bachthebinh2280600256.bachthebinh.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository; // Nhớ import User
import org.springframework.stereotype.Repository;

import com.bachthebinh2280600256.bachthebinh.entities.Order;
import com.bachthebinh2280600256.bachthebinh.entities.User;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user); // Tìm đơn hàng theo User
    List<Order> findAllByOrderByOrderDateDesc(); // Tìm tất cả đơn hàng, sắp xếp theo ngày đặt hàng giảm dần
}
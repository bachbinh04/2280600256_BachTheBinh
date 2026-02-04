package com.bachthebinh2280600256.bachthebinh.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // <--- QUAN TRỌNG: Phải có cái này để tự tạo getter/setter
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "order_details")
@Data // <--- QUAN TRỌNG
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // <--- Trường này sẽ tạo ra hàm setOrder()
}
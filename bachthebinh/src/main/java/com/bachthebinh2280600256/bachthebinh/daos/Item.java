package com.bachthebinh2280600256.bachthebinh.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Tự động tạo Getter, Setter, toString...
@AllArgsConstructor // Tự động tạo Constructor có tham số
@NoArgsConstructor  // Tự động tạo Constructor rỗng
public class Item {
    // Tên trường phải khớp chính xác với Cart.java (getBookId -> bookId)
    private Long bookId; 
    
    private String title;
    private Double price;
    private int quantity;
    private String image; // Trường ảnh mới thêm
}
package com.bachthebinh2280600256.bachthebinh.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long bookId;
    private String title;
    private double price;
    private int quantity;
}

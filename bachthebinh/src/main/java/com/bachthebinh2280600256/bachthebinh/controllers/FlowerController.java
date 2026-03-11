package com.bachthebinh2280600256.bachthebinh.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bachthebinh2280600256.bachthebinh.services.FlowerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/flowers") // Gom đường dẫn chung lên đây
@RequiredArgsConstructor // THÊM DÒNG NÀY ĐỂ FIX LỖI "not initialized"
public class FlowerController {
    
    private final FlowerService flowerService;

    @GetMapping
    public String listFlowers(Model model) {
        // Lấy danh sách hoa từ DB và truyền sang View
        model.addAttribute("flowers", flowerService.getAllFlowers());
        
        // Trả về đúng thư mục flowers/index.html của bạn
        return "flowers/index"; 
    }
}
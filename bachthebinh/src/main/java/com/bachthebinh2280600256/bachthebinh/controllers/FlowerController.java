package com.bachthebinh2280600256.bachthebinh.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bachthebinh2280600256.bachthebinh.entities.Flower;
import com.bachthebinh2280600256.bachthebinh.services.FlowerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/flowers")
@RequiredArgsConstructor
public class FlowerController {
    
    private final FlowerService flowerService;

    // Hiển thị trang chủ hoa
    @GetMapping
    public String listFlowers(Model model) {
        model.addAttribute("flowers", flowerService.getAllFlowers());
        return "flowers/index"; // Trả về file HTML trong thư mục templates/flowers/
    }

    // Xử lý thêm hoa mới từ form của Admin
    @PostMapping("/add")
    public String addFlower(@ModelAttribute Flower flower, 
                            @RequestParam("imageFile") MultipartFile imageFile) {
        
        // Xử lý lưu ảnh nếu người dùng có upload
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                // Đường dẫn lưu file vào thư mục static/images
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.write(path, imageFile.getBytes());
                
                // Cập nhật tên ảnh vào object flower
                flower.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Lưu sản phẩm vào Database
        flowerService.save(flower);
        
        // Load lại trang để xem kết quả
        return "redirect:/flowers";
    }
}
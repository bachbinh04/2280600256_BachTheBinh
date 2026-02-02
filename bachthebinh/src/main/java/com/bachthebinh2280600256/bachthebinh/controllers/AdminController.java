package com.bachthebinh2280600256.bachthebinh.controllers;

import com.bachthebinh2280600256.bachthebinh.entities.Role;
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.repositories.IRoleRepository;
import com.bachthebinh2280600256.bachthebinh.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final IRoleRepository roleRepository;

    // 1. Xem danh sách User
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user-list";
    }

    // 2. Form sửa quyền User
    @GetMapping("/users/edit/{id}")
    public String editUserRole(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll()); // Lấy tất cả quyền để chọn
        return "admin/user-edit";
    }

    // 3. Lưu quyền mới
    @PostMapping("/users/edit")
    public String saveUserRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userService.updateUserRole(userId, roleId);
        return "redirect:/admin/users";
    }
}
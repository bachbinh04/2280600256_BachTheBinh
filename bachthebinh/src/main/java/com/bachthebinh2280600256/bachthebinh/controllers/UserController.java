package com.bachthebinh2280600256.bachthebinh.controllers;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.services.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "user/register";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }
    // ... code cũ ...
    
    // 1. Hiển thị form profile
    @GetMapping("/profile")
    public String viewProfile(Model model, java.security.Principal principal) {
        String username = principal.getName(); // Lấy tên user đang đăng nhập
        User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "user/profile";
    }

    // 2. Lưu thông tin profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User user) {
        userService.updateUser(user); // Chỉ cập nhật email, phone, tên
        return "redirect:/profile?success";
    }
}
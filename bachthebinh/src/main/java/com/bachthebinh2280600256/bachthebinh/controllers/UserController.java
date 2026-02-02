package com.bachthebinh2280600256.bachthebinh.controllers;

import java.security.Principal;
import java.util.Optional; // <--- Thêm dòng này
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        String fullName = "New User";
        
        // Cờ đánh dấu: Có phải login bằng Google không?
        boolean isOauthLogin = false; 

        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            String email = oauthToken.getPrincipal().getAttribute("email");
            String name = oauthToken.getPrincipal().getAttribute("name");
            
            if (email != null) {
                username = email;
                isOauthLogin = true; // Đánh dấu là login Google
            }
            if (name != null) fullName = name;
        }

        String finalUsername = username;
        String finalName = fullName;
        boolean finalIsOauthLogin = isOauthLogin;

        // LOGIC MỚI:
        // 1. Tìm user trong DB
        Optional<User> existingUser;
        if (finalIsOauthLogin) {
            // Nếu là Google login, ưu tiên tìm bằng Email
            existingUser = userService.findByEmail(finalUsername); 
        } else {
            existingUser = userService.findByUsername(finalUsername);
        }

        // 2. Nếu không thấy và là Google login -> Tạo mới ngay lập tức
        User user = existingUser.orElseGet(() -> {
            if (finalIsOauthLogin) {
                userService.saveOauthUser(finalUsername, finalName);
                return userService.findByEmail(finalUsername).orElse(new User());
            }
            return new User();
        });
        
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
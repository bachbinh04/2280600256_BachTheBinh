package com.bachthebinh2280600256.bachthebinh.components;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.bachthebinh2280600256.bachthebinh.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. Lấy thông tin từ Google
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // 2. Lưu/Cập nhật vào Database
        userService.saveOauthUser(email, name);

        // 3. --- QUAN TRỌNG: Load lại User từ Database để lấy Role ---
        // (Lúc này SecurityContext mới biết user này là USER hay ADMIN)
        UserDetails userDetails = userService.loadUserByUsername(email);
        
        // 4. Tạo Authentication mới với đầy đủ quyền (Authorities) từ Database
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities() // Đây chính là Role lấy từ DB
        );
        
        // 5. Gán lại vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // 6. Chuyển hướng
        this.setDefaultTargetUrl("/books");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
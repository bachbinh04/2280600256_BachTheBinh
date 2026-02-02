package com.bachthebinh2280600256.bachthebinh;

import com.bachthebinh2280600256.bachthebinh.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
            // 1. Cấu hình CSRF (Tắt cho API để dễ test bằng Postman)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            // 2. Cấu hình quyền truy cập (Authorize)
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh và các trang Public
                .requestMatchers("/css/**", "/js/**", "/", "/register", "/error", "/login").permitAll()
                
                // API (Mở công khai để test, thực tế có thể cần khóa lại)
                .requestMatchers("/api/**").permitAll()
                
                // Các trang chỉ dành cho ADMIN
                .requestMatchers("/books/add", "/books/edit/**", "/books/delete/**").hasAuthority("ADMIN")
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // Trang quản lý User
                
                // Trang dành cho người đã đăng nhập (User/Admin)
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/books", "/cart", "/cart/**").hasAnyAuthority("ADMIN", "USER")
                
                // Tất cả các request khác đều cần đăng nhập
                .anyRequest().authenticated()
            )
            // 3. Cấu hình Đăng xuất
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Thêm param logout để hiện thông báo
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            // 4. Cấu hình Form Login (Đăng nhập thường)
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/books", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            // 5. Cấu hình OAuth2 Login (Đăng nhập Google)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/books", true)
            )
            // 6. Cấu hình Remember Me
            .rememberMe(rememberMe -> rememberMe
                .key("hutech")
                .rememberMeCookieName("hutech")
                .tokenValiditySeconds(24 * 60 * 60)
                .userDetailsService(userDetailsService())
            )
            // 7. Xử lý lỗi 403 (Không có quyền)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
            )
            // 8. Quản lý Session (Chỉ cho phép 1 thiết bị đăng nhập cùng lúc)
            .sessionManagement(sessionManagement -> sessionManagement
                .maximumSessions(1)
                .expiredUrl("/login")
            )
            .httpBasic(httpBasic -> httpBasic.realmName("hutech"))
            .build();
    }
}
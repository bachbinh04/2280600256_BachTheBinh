package com.bachthebinh2280600256.bachthebinh;

import org.springframework.context.annotation.Bean; // <--- 1. Import class này
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bachthebinh2280600256.bachthebinh.components.OAuth2LoginSuccessHandler;
import com.bachthebinh2280600256.bachthebinh.filter.JwtAuthFilter;
import com.bachthebinh2280600256.bachthebinh.services.UserService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthFilter jwtAuthFilter;
    
    // <--- 2. Inject Handler vào đây để Spring quản lý
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; 

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/", "/register", "/error", "/login").permitAll()
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/books/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/books/add", "/books/edit/**", "/books/delete/**").hasAuthority("ADMIN")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/books", "/cart", "/cart/**").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/books", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            // --- CẤU HÌNH OAUTH2 ĐÃ SỬA ---
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                // <--- 3. Thay dòng defaultSuccessUrl cũ bằng dòng này
                .successHandler(oAuth2LoginSuccessHandler) 
            )
            // ------------------------------
            .rememberMe(rememberMe -> rememberMe
                .key("hutech")
                .rememberMeCookieName("hutech")
                .tokenValiditySeconds(24 * 60 * 60)
                .userDetailsService(userDetailsService())
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/403")
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .maximumSessions(1)
                .expiredUrl("/login")
            )
            .httpBasic(httpBasic -> httpBasic.realmName("hutech"))
            .build();
    }
}
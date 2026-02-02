package com.bachthebinh2280600256.bachthebinh.controllers;

import com.bachthebinh2280600256.bachthebinh.models.AuthenticationRequest;
import com.bachthebinh2280600256.bachthebinh.models.AuthenticationResponse;
import com.bachthebinh2280600256.bachthebinh.services.JwtService;
import com.bachthebinh2280600256.bachthebinh.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateAndGetToken(@RequestBody AuthenticationRequest authRequest) {
        // 1. Xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // 2. Nếu đúng thì sinh Token trả về
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(token));
        } else {
            throw new UsernameNotFoundException("Sai thông tin đăng nhập!");
        }
    }
}
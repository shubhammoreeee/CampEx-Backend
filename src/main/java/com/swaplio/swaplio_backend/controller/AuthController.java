package com.swaplio.swaplio_backend.controller;

import com.swaplio.swaplio_backend.dto.auth.*;
import com.swaplio.swaplio_backend.model.User;
import com.swaplio.swaplio_backend.service.AuthService;
import com.swaplio.swaplio_backend.util.JwtUtil; // ✅ IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil; // ✅ ADD THIS

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/profile")
    public User updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart("data") UpdateProfileRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractEmail(jwt);

        return authService.updateProfile(email, request, image);
    }
}
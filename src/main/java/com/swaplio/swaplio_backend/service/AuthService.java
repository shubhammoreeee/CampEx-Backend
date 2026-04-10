package com.swaplio.swaplio_backend.service;

import com.swaplio.swaplio_backend.dto.auth.*;
import com.swaplio.swaplio_backend.model.User;
import com.swaplio.swaplio_backend.repository.UserRepository;
import com.swaplio.swaplio_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.swaplio.swaplio_backend.service.StorageService;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StorageService storageService;


    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                "USER"
        );

        User savedUser = userRepository.save(user);

        // ✅ Generate token here also
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                token
        );
    }

    public User updateProfile(String email, UpdateProfileRequest request, MultipartFile image) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());

        // ✅ DEBUG START
        System.out.println("IMAGE RECEIVED: " + image);

        if (image != null) {
            System.out.println("Image name: " + image.getOriginalFilename());
        }

        // ✅ Upload image
        if (image != null && !image.isEmpty()) {

            String imageUrl = storageService.uploadFile(image);

            System.out.println("IMAGE URL: " + imageUrl);

            user.setImageUrl(imageUrl);
        }

        // ✅ Before saving
        System.out.println("Saving user with image: " + user.getImageUrl());

        return userRepository.save(user);
    }
}
package com.swaplio.swaplio_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {

    private UUID id;
    private String name;
    private String email;
    private String token;
}
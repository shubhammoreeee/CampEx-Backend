package com.swaplio.swaplio_backend.dto.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    private String name;
    private String username;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
}
package com.example.usermanagementbackend.dto;

import lombok.Data;

@Data
public class LivreurDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private Long userId;
    private String photo; // Add this field
}
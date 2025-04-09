package com.example.usermanagementbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LivreurDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String vehicule;
    private Long userId; // Note: This is a Long, not a User object
}
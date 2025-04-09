package com.example.usermanagementbackend.dto;

import com.example.usermanagementbackend.entity.StatusLivraison;
import com.example.usermanagementbackend.entity.TypeLivraison;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LivraisonDTO {
    private Long id;
    private LocalDate dateLivraison;
    private StatusLivraison statusLivraison;
    private TypeLivraison typeLivraison;
    private LivreurDTO livreur; // Changed from Long livreurId to LivreurDTO
    private String photo;
    private String reason;
}
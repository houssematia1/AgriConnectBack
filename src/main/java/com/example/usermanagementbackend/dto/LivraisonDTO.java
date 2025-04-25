package com.example.usermanagementbackend.dto;

import com.example.usermanagementbackend.entity.StatusLivraison;
import com.example.usermanagementbackend.entity.TypeLivraison;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LivraisonDTO {
    private Long id;
    private LocalDate dateLivraison;
    private StatusLivraison statusLivraison;
    private TypeLivraison typeLivraison;
    private LivreurDTO livreur;
    private Long commandeId;
    private String photo;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String address;
    private Double carbonFootprint;
    private Double currentLat;
    private Double currentLng;
    private Double destinationLat;
    private Double destinationLng;
}
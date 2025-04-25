package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateLivraison;

    @Enumerated(EnumType.STRING)
    private StatusLivraison statusLivraison;

    @Enumerated(EnumType.STRING)
    private TypeLivraison typeLivraison;

    @ManyToOne
    private Livreur livreur;

    private Long commandeId;

    private String address;

    @Column(length = 1048576)
    private String photo;

    @Column(length = 500)
    private String reason;
    
    // Carbon footprint in kg CO2
    private Double carbonFootprint;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (dateLivraison == null) dateLivraison = LocalDate.now();
        if (statusLivraison == null) statusLivraison = StatusLivraison.TAKE_IT;
    }
}
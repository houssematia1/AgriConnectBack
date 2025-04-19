package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_nom")
    private String clientNom;

    private String statut;

    private String address;

    private String telephone;

    @Column(name = "livreur_id")
    private Long livreurId; // New field to store the Livreur ID

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
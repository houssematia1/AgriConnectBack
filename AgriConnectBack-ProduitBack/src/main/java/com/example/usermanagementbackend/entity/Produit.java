package com.example.usermanagementbackend.entity;

import com.example.usermanagementbackend.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private double prix;
    private String devise;
    private Double taxe;
    private String dateExpiration;
    private int stock;
    private int seuilMin;
    private String fournisseur;
    private Long fournisseurId;
    private boolean autoReapprovisionnement;
    private int quantiteReapprovisionnement;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String image;
}
package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PRODUITS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    private String nom;

    private double prix;

    @Column(length = 3)
    private String devise; // EUR, USD, etc.

    private Integer taxe; // Pourcentage de taxe appliqué

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_expiration")
    private Date dateExpiration;

    private String fournisseur; // Nom du fournisseur

    @Column(name = "fournisseur_id")
    private Long fournisseurId; // Lien vers l'agriculteur

    private String image; // Image du produit

    private int stock; // Quantité en stock

    private int seuilMin; // Seuil minimal pour alerte

    private boolean autoReapprovisionnement; // Activer/Désactiver auto-réappro

    private int quantiteReapprovisionnement; // Quantité pour auto-réappro

    @Column(name = "sales_count")
    private Integer salesCount = 0; // New field to track sales count, initialized to 0

    @ManyToMany(mappedBy = "produits")
    private List<Promotion> promotions = new ArrayList<>();
}
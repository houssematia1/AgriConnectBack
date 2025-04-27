package com.example.usermanagementbackend.entity;

import com.example.usermanagementbackend.enums.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_expiration")
    private Date dateExpiration;

    private String description;
    private double prix;
    private String devise;
    private Double taxe;

    private int stock;
    private int seuilMin;
    private String fournisseur;
    private Long fournisseurId;
    private boolean autoReapprovisionnement;
    private int quantiteReapprovisionnement;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String image;
    @Column(name = "sales_count", nullable = false, columnDefinition = "integer default 0")
    private Integer salesCount = 0;  // Initialize with default value

    @ManyToMany(mappedBy = "produits")
    @JsonBackReference
    private List<Promotion> promotions = new ArrayList<>();
}
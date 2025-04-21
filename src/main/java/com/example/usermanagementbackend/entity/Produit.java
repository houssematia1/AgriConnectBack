package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String devise;

    private Integer taxe;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_expiration")
    private Date dateExpiration;

    private String fournisseur;

    @Column(name = "fournisseur_id")
    private Long fournisseurId;

    private String image;
    private int stock;
    private int seuilMin;
    private boolean autoReapprovisionnement;
    private int quantiteReapprovisionnement;

    @Column(name = "sales_count")
    private Integer salesCount = 0;

    @ManyToMany(mappedBy = "produits")
    @JsonBackReference
    private List<Promotion> promotions = new ArrayList<>();
}
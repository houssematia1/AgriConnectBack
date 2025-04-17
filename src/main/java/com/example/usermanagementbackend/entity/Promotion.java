package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private double pourcentageReduction;

    @Column(name = "date_debut")
    private Date dateDebut; // Utilisation de Date

    @Column(name = "date_fin")
    private Date dateFin; // Utilisation de Date

    @Column(name = "condition_promotion")
    private String conditionPromotion;

    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "promotion_produit",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<Produit> produits;
}
package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ligne_commande")
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "num_commande")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "ref_produit")
    @JsonIgnore
    private Produit produit;
    @ManyToOne
    private Facture facture;

    private int qte;
    private double prixUnitaire;
    private double total;
    private double ttc;
}
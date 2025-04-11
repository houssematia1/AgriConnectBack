package com.example.usermanagementbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ligneCommande")

public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;
    @ManyToOne
    @JoinColumn(name="NUM_COMMANDE")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name="REF_PRODUIT")
    @JsonIgnore
    private Produit produit;

    private int qte;
    private double total;
    private double ttc;


}

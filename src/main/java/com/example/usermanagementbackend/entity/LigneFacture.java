package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ligneFacture")

public class LigneFacture  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="NUM_FACTURE")
    private Facture facture;

    @ManyToOne
    @JoinColumn(name="REF_PRODUIT")
    private Produit produit;

    private int qte;
    private double prix;
    private double total;
    private double ttc;


}

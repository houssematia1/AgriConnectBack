package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private Integer qte;
    private Double prixUnitaire;
    private Double total;
    private Double ttc;
}
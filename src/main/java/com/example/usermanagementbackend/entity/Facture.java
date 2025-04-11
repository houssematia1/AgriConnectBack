package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "facture")

public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montantTotal;
    private String numeroFacture;
    private Date dateFacture;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

}
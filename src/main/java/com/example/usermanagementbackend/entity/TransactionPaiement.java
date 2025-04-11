package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "TransactionPaiement")
public class TransactionPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;
    private String methodePaiement;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTransaction;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    public void listCommande(Commande commande) {

    }

}
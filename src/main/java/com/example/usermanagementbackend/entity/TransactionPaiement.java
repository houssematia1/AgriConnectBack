package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction_paiement")
public class TransactionPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;
    private String methodePaiement;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTransaction;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;
}
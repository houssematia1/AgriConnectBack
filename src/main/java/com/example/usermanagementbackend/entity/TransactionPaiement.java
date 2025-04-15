package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private Double montant;
    private String methodePaiement;
    private LocalDateTime dateTransaction;
    private String paymentStatus;
    private String paymentGatewayReference;
}
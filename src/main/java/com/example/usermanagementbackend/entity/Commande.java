package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "commande")

public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientNom;
    private String statut;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<TransactionPaiement> transactions;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Facture> factures;

}
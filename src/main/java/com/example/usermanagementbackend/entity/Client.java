package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orders", "transactions", "factures"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private Double creditLimit;
    private String preferences;

    @OneToMany(mappedBy = "client")
    private List<Commande> orders;

    @OneToMany(mappedBy = "client")
    private List<TransactionPaiement> transactions;

    @OneToMany(mappedBy = "client")
    private List<Facture> factures;
}
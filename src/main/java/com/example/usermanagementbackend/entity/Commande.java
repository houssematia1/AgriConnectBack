package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"lignesCommande", "transactions", "factures"})
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDate dateCreation;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "gouvernement")
    private String gouvernement;

    @Column(name = "adresse")
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<LigneCommande> lignesCommande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<TransactionPaiement> transactions;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Facture> factures;
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED,PAID
    }
}
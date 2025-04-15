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
@ToString(exclude = "lignesFacture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    private Double montantTotal;
    private LocalDate dateFacture;
    private String numeroFacture;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<LigneFacture> lignesFacture;
}
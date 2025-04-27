package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "purchase")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToMany(fetch = FetchType.EAGER)    @JoinTable(
            name = "purchase_produit",
            joinColumns = @JoinColumn(name = "purchase_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<Produit> produits = new ArrayList<>();

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private double totalPrice;


    @Column(name = "purchase_date", nullable = false)    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAchat = new Date();


}
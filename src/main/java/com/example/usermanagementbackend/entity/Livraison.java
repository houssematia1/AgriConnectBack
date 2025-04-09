package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateLivraison;

    @Enumerated(EnumType.STRING)
    private StatusLivraison statusLivraison;

    @Enumerated(EnumType.STRING)
    private TypeLivraison typeLivraison;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livreur_id", nullable = false)
    private Livreur livreur;

    @Column(length = 1048576)
    private String photo;

    private String reason;
}


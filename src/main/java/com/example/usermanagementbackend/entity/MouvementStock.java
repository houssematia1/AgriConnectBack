package com.example.usermanagementbackend.entity;

import com.example.usermanagementbackend.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "mouvements_stock")
@Getter
@Setter
@NoArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    private int quantite;
    private Date dateMouvement;
}
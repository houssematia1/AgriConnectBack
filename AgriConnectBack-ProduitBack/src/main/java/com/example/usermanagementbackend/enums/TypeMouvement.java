package com.example.usermanagementbackend.enums;

public enum TypeMouvement {
    ENTREE,  // Ajout de stock
    SORTIE,  // Vente ou retrait
    PERTE,
    VENTE,   // Stock perdu (ex : périmé ou endommagé)
          // Don aux associations
}
